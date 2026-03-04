import java.time.LocalDateTime;

import java.sql.Date;

import java.sql.*;
import java.util.Properties;

import java.io.*;

import java.util.List;


public class databaseConnection {

    private Connection conn;
	
	private final String ChatHistoryBasePath = "serverFiles\\Chats\\Chat";
	
	private messageFileEnterpreter mfe;
	
    public databaseConnection()
            throws SQLException, ClassNotFoundException {
		
		Class.forName("org.postgresql.Driver");
		
		Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
		
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/", props);
		
		
		mfe = new messageFileEnterpreter(ChatHistoryBasePath);
    }


    public String createUser(String username, String displayName, String passwordHash) {
        String sql = """
                INSERT INTO app_user (username, display_name, password_hash)
                VALUES (?, ?, ?)
                RETURNING user_id
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, username);
            st.setString(2, displayName);
            st.setString(3, passwordHash);

            ResultSet rs = st.executeQuery();
            rs.next();
            long userId = rs.getLong(1);

            return "{\"success\":true, \"userId\":" + userId + "}";

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + e + "\"}";
        }
    }


    public SQLQueryResult login(String username, String passwordHash) {
        String sql = """
                SELECT user_id, display_name
                FROM app_user
                WHERE username = ?
                  AND password_hash = ?
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, username);
            st.setString(2, passwordHash);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("user_id");


                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE app_user SET last_login_at = now() WHERE user_id = ?")) {
                    upd.setLong(1, userId);
                    upd.executeUpdate();
                }

                return new SQLQueryResult(true, new String[] {"display_name", rs.getString("display_name"), "userId", userId + ""});
            }

            return new SQLQueryResult(false, new String[] {"error", "Invalid login"});

        } catch (SQLException e) {
            return new SQLQueryResult(false, new String[] {"error", e.toString()});
        }
    }


    public SQLQueryResult createChat(String chatName, long createdBy) throws IOException, FileNotFoundException{
        String sql = """
                INSERT INTO chat (chat_name, created_by, is_direct)
                VALUES (?, ?, false)
                RETURNING chat_id
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, chatName);
            st.setLong(2, createdBy);

            ResultSet rs = st.executeQuery();
            rs.next();
            long chatId = rs.getLong(1);
			
            addMember(chatId, createdBy, "owner");
			
			mfe.createChatCashe(chatId);
			
            return new SQLQueryResult(true, new String[] {"chatId", chatId +""});

        } catch (SQLException e) {
            return new SQLQueryResult(false, new String[] {"error", e.toString()});
        }
    }


    public SQLQueryResult addMember(long chatId, long userId, String role) {
        String sql = """
                INSERT INTO chat_member (chat_id, user_id, role)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, chatId);
            st.setLong(2, userId);
            st.setString(3, role);

            st.executeUpdate();
            return new SQLQueryResult(true, new String[] {});

        } catch (SQLException e) {
            return new SQLQueryResult(false, new String[] {"error", e.toString()});
        }
    }


    private boolean isMember(long chatId, long userId) throws SQLException {
        String sql = """
                SELECT 1
                FROM chat_member
                WHERE chat_id = ?
                  AND user_id = ?
                """;
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, chatId);
            st.setLong(2, userId);
            ResultSet rs = st.executeQuery();
            return rs.next();
        }
    }


    public SQLQueryResult saveMessage(long chatID, Message msg) throws IOException {
		
		return new SQLQueryResult( mfe.saveMessage(chatID, msg) );
	}


    public List<byte[]> loadChatHistory(long chatID, LocalDateTime lastModifide) throws IOException {
        
		return mfe.loadRawMessages(chatID, lastModifide);
    }


    public SQLQueryResult listChats(long userId) {
        String sql = """
                SELECT c.chat_id, c.chat_name,  c.is_direct,  c.created_at
                FROM chat c
                JOIN chat_member m ON m.chat_id = c.chat_id
                WHERE m.user_id = ? ORDER BY c.created_at
                """;
		
		
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, userId);
            ResultSet rs = st.executeQuery();
            
			
			SQLQueryResult out = new SQLQueryResult(true, new String[] {});
			
			int i = 0;
			while (rs.next()) {
				out = out.insertNewPair(i + "-chat_id", rs.getString("chat_id"));
				
				out = out.insertNewPair(i + "-chat_name", rs.getString("chat_name"));
				
				i += 1;
            }
			
			out = out.insertNewPair("length", i + "");
			
			return out;

        } catch (SQLException e) {
            return new SQLQueryResult(false, new String[] {"error", e.toString()});
        }
    }
}

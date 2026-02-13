import java.sql.*;        
import java.util.Properties;

public class ChatConnection {


    static final String DBNAME = "chatdb";
    static final String DATABASE = "jdbc:postgresql://localhost/" + DBNAME;
    static final String USERNAME = "postgres";
    static final String PASSWORD = "1234";

    private Connection conn;

    public ChatConnection() throws SQLException, ClassNotFoundException {
        this(DATABASE, USERNAME, PASSWORD);
    }

    public ChatConnection(String db, String user, String pwd)
            throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pwd);

        conn = DriverManager.getConnection(db, props);
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
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


    public String login(String username, String passwordHash) {
        String sql = """
                SELECT user_id
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

                return "{\"success\":true, \"userId\":" + userId + "}";
            }

            return "{\"success\":false, \"error\":\"Invalid login\"}";

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


    public String createChat(String chatName, long createdBy) {
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

            return "{\"success\":true, \"chatId\":" + chatId + "}";

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


    public String addMember(long chatId, long userId, String role) {
        String sql = """
                INSERT INTO chat_member (chat_id, user_id, role)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, chatId);
            st.setLong(2, userId);
            st.setString(3, role);

            st.executeUpdate();
            return "{\"success\":true}";

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
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


    public String sendTextMessage(long chatId, long senderId, String textBody) {
        String sql = """
                INSERT INTO message (chat_id, sender_id, text_body)
                VALUES (?, ?, ?)
                RETURNING message_id
                """;

        try {
            if (!isMember(chatId, senderId)) {
                return "{\"success\":false, \"error\":\"Not a member of this chat\"}";
            }

            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setLong(1, chatId);
                st.setLong(2, senderId);
                st.setString(3, textBody);

                ResultSet rs = st.executeQuery();
                rs.next();
                long messageId = rs.getLong(1);

                return "{\"success\":true, \"messageId\":" + messageId + "}";
            }

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


    public String loadChatHistory(long chatId) {
        String sql = """
                SELECT COALESCE(
                  json_agg(
                    json_build_object(
                      'messageId', m.message_id,
                      'senderId', m.sender_id,
                      'sender', u.username,
                      'sentAt', m.sent_at,
                      'text', m.text_body,
                      'imagePath', m.image_path,
                      'imageMime', m.image_mime,
                      'imageSize', m.image_size
                    )
                    ORDER BY m.sent_at
                  ),
                  '[]'::json
                )
                FROM message m
                JOIN app_user u ON u.user_id = m.sender_id
                WHERE m.chat_id = ?
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, chatId);
            ResultSet rs = st.executeQuery();
            rs.next();
            return rs.getString(1);

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


    public String listChats(long userId) {
        String sql = """
                SELECT COALESCE(
                  json_agg(
                    json_build_object(
                      'chatId', c.chat_id,
                      'name', c.chat_name,
                      'isDirect', c.is_direct,
                      'createdAt', c.created_at
                    )
                    ORDER BY c.created_at
                  ),
                  '[]'::json
                )
                FROM chat c
                JOIN chat_member m ON m.chat_id = c.chat_id
                WHERE m.user_id = ?
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, userId);
            ResultSet rs = st.executeQuery();
            rs.next();
            return rs.getString(1);

        } catch (SQLException e) {
            return "{\"success\":false, \"error\":\"" + getError(e) + "\"}";
        }
    }


        String message = e.getMessage();
        int ix = message.indexOf('\n');
        if (ix > 0) message = message.substring(0, ix);
        message = message.replace("\"", "\\\"");
        return message;
    }
}

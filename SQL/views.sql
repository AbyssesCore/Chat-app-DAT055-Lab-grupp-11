CREATE VIEW chat_history AS
SELECT
  m.message_id,
  m.chat_id,
  m.sent_at,
  u.user_id AS sender_id,
  u.display_name AS sender_name,
  m.text_body,
  m.image_path,
  m.image_mime,
  m.image_size
FROM message m
JOIN app_user u ON u.user_id = m.sender_id; 
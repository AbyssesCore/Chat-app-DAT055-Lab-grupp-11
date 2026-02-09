\set QUIET true
SET client_min_messages TO WARNING; -- Less talk please.
-- This script deletes everything in your database
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO CURRENT_USER;
-- This line makes psql stop on the first error it encounters
-- You may want to remove this when running tests that are intended to fail
\set ON_ERROR_STOP ON
SET client_min_messages TO NOTICE; -- More talk
\set QUIET false

CREATE TABLE app_user (
  user_id        BIGSERIAL PRIMARY KEY,
  username       TEXT NOT NULL UNIQUE,
  display_name   TEXT NOT NULL,
  password_hash  TEXT NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_login_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2) CHATS / ROOMS (chat instances)
CREATE TABLE chat (
  chat_id     BIGSERIAL PRIMARY KEY,
  chat_name   TEXT NOT NULL,
  created_by  BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE RESTRICT,
  is_direct   BOOLEAN NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Optional: prevent duplicate room names if you want:
-- CREATE UNIQUE INDEX chat_name_unique ON chat(chat_name);

-- 3) MEMBERSHIP (who belongs to which chat)
CREATE TABLE chat_member (
  chat_id    BIGINT NOT NULL REFERENCES chat(chat_id) ON DELETE CASCADE,
  user_id    BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
  joined_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  role       TEXT NOT NULL DEFAULT 'member', -- 'owner', 'member' (simple)
  PRIMARY KEY (chat_id, user_id)
);

CREATE INDEX chat_member_user_idx ON chat_member(user_id);

-- 4) MESSAGES (text OR image)
-- We store both types in one table, and enforce "exactly one payload" with a CHECK constraint.
CREATE TABLE message (
  message_id     BIGSERIAL PRIMARY KEY,
  chat_id        BIGINT NOT NULL REFERENCES chat(chat_id) ON DELETE CASCADE,
  sender_id      BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE RESTRICT,
  sent_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

  -- payload
  text_body      TEXT,
  image_path     TEXT,       -- e.g., relative path, filename, or URL
  image_mime     TEXT,       -- e.g., 'image/png'
  image_size     INTEGER,    -- bytes (optional)

  -- Simple uniqueness option: a client-generated UUID (useful when syncing)
  client_uuid    UUID UNIQUE,

  CONSTRAINT message_payload_check
    CHECK (
      (text_body IS NOT NULL AND image_path IS NULL)
      OR
      (text_body IS NULL AND image_path IS NOT NULL)
    ),

  CONSTRAINT message_image_metadata_check
    CHECK (
      image_path IS NULL
      OR (image_mime IS NOT NULL AND image_size IS NOT NULL AND image_size >= 0)
    )
);

-- Fast "load chat history" for a chat
CREATE INDEX message_chat_time_idx ON message(chat_id, sent_at);

-- Prevent sending messages to chats you are not member of:
-- (This is hard to enforce with a pure CHECK; typically handled in app logic or trigger.)
-- We'll keep it simple and do it in app logic.

-- 5) RECEIPTS (optional but very useful)
-- Makes "receiver(s)" explicit: who received/read which message.
-- For group chats, there are many receivers.
CREATE TABLE message_receipt (
  message_id   BIGINT NOT NULL REFERENCES message(message_id) ON DELETE CASCADE,
  user_id      BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
  delivered_at TIMESTAMPTZ,
  read_at      TIMESTAMPTZ,
  PRIMARY KEY (message_id, user_id),

  CONSTRAINT receipt_times_check
    CHECK (
      delivered_at IS NULL OR read_at IS NULL OR delivered_at <= read_at
    )
);

CREATE INDEX message_receipt_user_idx ON message_receipt(user_id);
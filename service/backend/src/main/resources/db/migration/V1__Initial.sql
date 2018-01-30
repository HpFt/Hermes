CREATE TABLE users
(
  id VARCHAR(36) PRIMARY KEY,
  ip VARCHAR(16) NOT NULL UNIQUE,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE files
(
  id VARCHAR(36) PRIMARY KEY,
  size BIGINT NOT NULL,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL,
  hash VARCHAR(64) NOT NULL,
  path TEXT NOT NULL
);

CREATE TABLE files_users
(
  id VARCHAR(36) NOT NULL,
  file_id VARCHAR(36) NOT NULL
    CONSTRAINT files_users_files_id_fk
    REFERENCES files
    ON UPDATE CASCADE ON DELETE CASCADE,
  user_id VARCHAR(36) NOT NULL
    CONSTRAINT files_users_users_id_fk
    REFERENCES users
    ON UPDATE CASCADE ON DELETE CASCADE,
  expiration TIMESTAMP WITH TIME ZONE NOT NULL,
  max_downloads BIGINT,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX files_users_file_id_user_id_uindex
  ON files_users (file_id, user_id);


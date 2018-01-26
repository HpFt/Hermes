CREATE TABLE files
(
  id        VARCHAR(36) PRIMARY KEY,
  size      BIGINT                   NOT NULL,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  hash      VARCHAR(64)              NOT NULL,
  path      TEXT                     NOT NULL
);

CREATE TABLE files_users
(
  id            VARCHAR(36) PRIMARY KEY,
  file_id       VARCHAR(36)              NOT NULL,
  user_id       VARCHAR(36)              NOT NULL,
  constraint_id VARCHAR(36)              NOT NULL,
  create_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE files_constraints
(
  id            VARCHAR(36) PRIMARY KEY,
  expiration    TIMESTAMP WITH TIME ZONE NOT NULL,
  max_downloads BIGINT
)
-- CREATE SCHEMA core;

CREATE TABLE users
(
  id      VARCHAR(36) PRIMARY KEY,
  ip      VARCHAR(16)              NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE tokens
(
  token   VARCHAR PRIMARY KEY,
  user_id VARCHAR(16)              NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
-- CREATE SCHEMA core;

CREATE TABLE users
(
  id        VARCHAR(36) PRIMARY KEY,
  ip        VARCHAR(16)              NOT NULL UNIQUE,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
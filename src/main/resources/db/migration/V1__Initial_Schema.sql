--
-- Schema settings
--
SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;

--
-- TYPES
--
CREATE TYPE user_gender AS ENUM ('m', 'f');

--
-- TABLES
--
CREATE TABLE users (
  id         BIGINT       NOT NULL,
  email      VARCHAR(100) NOT NULL,
  first_name VARCHAR(50)  NOT NULL,
  last_name  VARCHAR(50)  NOT NULL,
  gender     user_gender  NOT NULL,
  birth_date TIMESTAMP    NOT NULL
);

CREATE TABLE locations (
  id          BIGINT      NOT NULL,
  place       TEXT        NOT NULL,
  country     VARCHAR(50) NOT NULL,
  city        VARCHAR(50) NOT NULL,
  distance    BIGINT      NOT NULL,
  visit_count BIGINT      NOT NULL DEFAULT 0,
  marks_sum   BIGINT      NOT NULL DEFAULT 0
);

CREATE TABLE visits (
  id          BIGINT    NOT NULL,
  location_id BIGINT    NOT NULL,
  user_id     BIGINT    NOT NULL,
  visited_at  TIMESTAMP NOT NULL,
  mark        SMALLINT  NOT NULL
);

--
-- Primary Keys
--
ALTER TABLE ONLY users     ADD CONSTRAINT pk_users     PRIMARY KEY (id);
ALTER TABLE ONLY locations ADD CONSTRAINT pk_locations PRIMARY KEY (id);
ALTER TABLE ONLY visits    ADD CONSTRAINT pk_visits    PRIMARY KEY (id);

--
-- UK and Indexes
--
ALTER TABLE ONLY users ADD CONSTRAINT uk_users__email UNIQUE (email);
CREATE INDEX ix_users__gender ON users (gender);
CREATE INDEX ix_users__birth_date ON users (birth_date);


CREATE INDEX ix_locations__country ON locations (country);
CREATE INDEX ix_locations__distance ON locations (distance);

CREATE INDEX ix_ag_visits__locations__location_id ON visits (location_id);
CREATE INDEX ix_ag_visits__users__user_id ON visits (user_id);
CREATE INDEX ix_visits__visited_at ON visits (visited_at);
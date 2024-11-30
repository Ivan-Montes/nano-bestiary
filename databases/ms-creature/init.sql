DROP DATABASE IF EXISTS creatureReadDb;

CREATE DATABASE creatureReadDb;

DROP TABLE IF EXISTS creatures;

CREATE TABLE creatures(
	creature_id uuid NOT NULL,
	creature_name VARCHAR(255) NOT NULL UNIQUE,
	creature_description VARCHAR(255) NOT NULL,
	area_id uuid NOT NULL,
	CONSTRAINT creatures_PK PRIMARY KEY(creature_id)
);
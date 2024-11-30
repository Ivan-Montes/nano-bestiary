DROP DATABASE IF EXISTS areaReadDb;

CREATE DATABASE areaReadDb;

DROP TABLE IF EXISTS areas;

CREATE TABLE areas(
	area_id uuid NOT NULL,
	area_name varchar(255) NOT NULL UNIQUE,
	CONSTRAINT areas_PK PRIMARY KEY(area_id)
);



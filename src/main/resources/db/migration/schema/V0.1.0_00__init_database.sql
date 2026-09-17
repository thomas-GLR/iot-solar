-- Init database

CREATE TABLE reading_device
(
    id   int8         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT reading_device_pkey PRIMARY KEY (id),
    CONSTRAINT reading_device_name_uk UNIQUE (name)
);

CREATE SEQUENCE reading_device_id_seq INCREMENT BY 50 START 1;

CREATE TABLE temperatures
(
    id                int8           NOT NULL,
    collection_date   TIMESTAMP      NOT NULL,
    value             NUMERIC(10, 2) NOT NULL,
    reading_device_id int8           NOT NULL
        CONSTRAINT temperatures_reading_device_id_fk REFERENCES reading_device (id),
    CONSTRAINT temperatures_pkey PRIMARY KEY (id),
    CONSTRAINT temperatures_collection_date_value_reading_device_id_uk UNIQUE (reading_device_id, collection_date, value)
);

CREATE SEQUENCE temperatures_id_seq INCREMENT BY 50 START 1;

CREATE TABLE resistance_state
(
    id              int8      NOT NULL,
    current_state   BOOLEAN   NULL,
    requested_state BOOLEAN   NOT NULL,
    last_update     TIMESTAMP NOT NULL,
    CONSTRAINT resistance_state_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE resistance_state_id_seq INCREMENT BY 50 START 1;

CREATE TABLE users
(
    id            int8         NOT NULL,
    username      VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255),
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_uk UNIQUE (username)
);

CREATE SEQUENCE users_id_seq INCREMENT BY 50 START 1;

CREATE TABLE roles
(
    id   int8         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_name_uk UNIQUE (name)
);

CREATE SEQUENCE roles_id_seq INCREMENT BY 1 START 1;

CREATE TABLE user_roles
(
    user_id int8 NOT NULL
        CONSTRAINT user_roles_user_id_fk REFERENCES users (id),
    role_id int8 NOT NULL
        CONSTRAINT user_roles_role_id_fk REFERENCES roles (id),
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id)
);

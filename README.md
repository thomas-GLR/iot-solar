# API communiquant avec l'applciation IOT solar et controllant l'ESP32

## Création de la BDD
``` postgresql
> psql -U postgres
create role admin superuser login;
create role iotsolar login;
alter role iotsolar with password iotsolar
create role read_only login;
create role read_write login;
create database {iotsolar owner iotsolar;
\c iotsolar;
drop schema public;
create schema public authorization iotsolar;
exit
```

## Script pour supprimer la BDD
``` postgresql
drop table if exists esp_parameter cascade;
drop table if exists users cascade;
drop table if exists temperatures cascade;
drop table if exists reading_device cascade;
drop table if exists resistance_state cascade;

DROP SEQUENCE if exists esp_parameter_id_seq;
DROP SEQUENCE if exists users_id_seq;
DROP SEQUENCE if exists temperatures_id_seq;
DROP SEQUENCE if exists reading_device_id_seq;
DROP SEQUENCE if exists resistance_state_id_seq;

delete from flyway_schema_history;
```

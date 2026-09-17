-- Insert TOP, MIDDLE and BOTTOM reading device

INSERT INTO roles (id, name)
VALUES (nextval('roles_id_seq'),'ROLE_NEW'),
       (nextval('roles_id_seq'),'ROLE_USER'),
       (nextval('roles_id_seq'),'ROLE_ADMIN');
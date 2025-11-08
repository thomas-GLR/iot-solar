-- Insert TOP, MIDDLE and BOTTOM reading device

INSERT INTO reading_device (id, name)
VALUES (nextval('reading_device_id_seq'),'TOP'),
       (nextval('reading_device_id_seq'),'MIDDLE'),
       (nextval('reading_device_id_seq'),'BOTTOM');
INSERT INTO users (name, login, password,color,is_in_ban)
SELECT 'me','nikitos','$2a$10$Ov6XK2ScooVBUQ0xhd7CUu7hR8eMwIBpeNa0Uzi17Tcze8WQXv2eO','#808080',false
    Where not exists(select * from users where id = 1) ON CONFLICT DO NOTHING ;
INSERT INTO roles (role)VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (role)VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO  user_roles(user_id, role_id) values (1,1) ON CONFLICT DO NOTHING;


INSERT INTO privileges (privilege)VALUES ('CREATOR') ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)VALUES ('OWNER') ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)VALUES ('MODERATOR') ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)VALUES ('READER') ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)VALUES ('NONE') ON CONFLICT DO NOTHING;

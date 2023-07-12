INSERT INTO users (name, login, password, role,color,is_in_ban)
SELECT 'me','nikitos','$2a$10$Ov6XK2ScooVBUQ0xhd7CUu7hR8eMwIBpeNa0Uzi17Tcze8WQXv2eO','ROLE_ADMIN','#808080',false
    Where not exists(select * from users where id = 1);
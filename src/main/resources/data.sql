INSERT INTO users (id,name, login, password, role)
SELECT 1,'me','nikitos','$2a$10$Ov6XK2ScooVBUQ0xhd7CUu7hR8eMwIBpeNa0Uzi17Tcze8WQXv2eO','ROLE_ADMIN'
    Where not exists(select * from users where id = 1);
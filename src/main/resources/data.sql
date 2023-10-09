INSERT INTO users (name, login, password, color, is_in_ban)
SELECT 'me', 'nikitos', '$2a$10$33iofx7fmbLYMYO/WgtGhe02WAgBin9CHcBh.ZhygwBxryJYLs0jO', '#808080', false
Where not exists(select * from users where id = 1)
ON CONFLICT DO NOTHING;
-- INSERT INTO users (name, login, password,color,is_in_ban)
-- SELECT 'me','timan','$2a$10$G/n6K7e4K8/VhAbtT.cxtOwq7wT0J6hukxpeSZsJkHTBSIAX7MMAi','#808080',false
--     Where not exists(select * from users where id = 2) ON CONFLICT DO NOTHING ;

-- insert into users(name, login, password, color, is_in_ban)
-- VALUES ('me','timan','$2a$10$G/n6K7e4K8/VhAbtT.cxtOwq7wT0J6hukxpeSZsJkHTBSIAX7MMAi','#808080',false)
-- ON CONFLICT  DO NOTHING;

INSERT INTO roles (role)
VALUES ('ADMIN')
ON CONFLICT DO NOTHING;
INSERT INTO roles (role)
VALUES ('USER')
ON CONFLICT DO NOTHING;
INSERT INTO user_roles(user_id, role_id)
values (1, 1)
ON CONFLICT DO NOTHING;
-- INSERT INTO  user_roles(user_id, role_id) values (2,1) ON CONFLICT DO NOTHING;


INSERT INTO privileges (privilege)
VALUES ('CREATOR')
ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)
VALUES ('OWNER')
ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)
VALUES ('MODERATOR')
ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)
VALUES ('READER')
ON CONFLICT DO NOTHING;
INSERT INTO privileges (privilege)
VALUES ('NONE')
ON CONFLICT DO NOTHING;

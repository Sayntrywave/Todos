CREATE TABLE IF NOT EXISTS users(
    id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    login VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR(80) NOT NULL,
--     privilege VARCHAR(30),
    is_in_ban boolean
);

CREATE TABLE IF NOT EXISTS todos(
      id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
      title VARCHAR(100),
      description varchar(450),
      is_completed boolean DEFAULT FALSE,
      time_spent int DEFAULT null
);

CREATE TABLE IF NOT EXISTS  roles(
      id int primary key generated by default as identity,
      role varchar(20) unique not null
);
CREATE TABLE IF NOT EXISTS privileges(
      id int primary key generated by default as identity,
      privilege varchar(20) unique not null
);

CREATE TABLE IF NOT EXISTS user_roles(
       user_id int references users(id),
       role_id int references roles(id),
       primary key (user_id,role_id)
);

CREATE TABLE IF NOT EXISTS todos_users(
        id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
        todo_id int REFERENCES todos(id),
        user_id int REFERENCES users(id),
        role_id int references privileges(id)
);



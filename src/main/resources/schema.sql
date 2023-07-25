CREATE TABLE IF NOT EXISTS users(
    id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(15),
    login VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR(80) NOT NULL,
--     role VARCHAR(30),
    color VARCHAR(30),
    is_in_ban boolean
);

CREATE TABLE IF NOT EXISTS todos(
      id int GENERATED BY DEFAULT AS IDENTITY,
      user_id int REFERENCES users(id) ON DELETE  SET NULL ,
      title VARCHAR(30),
      description varchar(300),
      is_completed boolean DEFAULT FALSE,
      time_spent int DEFAULT null
);



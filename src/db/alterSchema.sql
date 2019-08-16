CREATE UNIQUE INDEX UIX_USER_NAME ON users (upper(user_name));
CREATE UNIQUE INDEX UIX_EMAIL ON users (upper(email));
CREATE UNIQUE INDEX UIX_ROLE_NAME ON role (upper(name));
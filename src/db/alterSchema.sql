CREATE UNIQUE INDEX UIX_USER_NAME ON users (lower(user_name));
CREATE UNIQUE INDEX UIX_ROLE_NAME ON role (lower(name));
CREATE UNIQUE INDEX UIX_USER_NAME ON users (upper(username));
CREATE UNIQUE INDEX UIX_EMAIL ON users (upper(email));
CREATE UNIQUE INDEX UIX_ROLE_NAME ON role (upper(name));
CREATE INDEX IX_USER_LAST_NAME ON users (upper(last_name));
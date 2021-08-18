INSERT INTO USERS (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, ACCOUNT_ENABLED, EMAIL, FIRST_NAME, INVALID_LOGIN_ATTEMPTS, LAST_INVALID_LOGIN_ATTEMPT, LAST_LOGGED_IN, LAST_NAME, LAST_PASSWORD_CHANGE, MIDDLE_INITIAL, PASSWORD, PASSWORD_CHANGE_ID, PASSWORD_CHG_ID_CREATE_TIME, TIME_ZONE, USERNAME) VALUES ('9DD05B17EAAE45259BB787D99377901F', SYSDATE, SYSDATE, 0, 'Y', 'lee.greiner@duke.edu', 'Lee', null, null, null, 'Greiner', SYSDATE, null, '$2a$10$JNW.5B0oqjHxvdHatkgdkeItLH4L2awVeigR7vu1NgBUYOVYU6fo.', '9DD05B17EAAE45259BB787D99377901E', SYSDATE,  'UTC', 'grein003');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO USERS_AUD (USER_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, ACCOUNT_ENABLED, EMAIL, FIRST_NAME, INVALID_LOGIN_ATTEMPTS, LAST_INVALID_LOGIN_ATTEMPT, LAST_LOGGED_IN, LAST_NAME, LAST_PASSWORD_CHANGE, MIDDLE_INITIAL, PASSWORD, PASSWORD_CHANGE_ID, PASSWORD_CHG_ID_CREATE_TIME, TIME_ZONE, USERNAME, REV, REVTYPE) VALUES ((select user_id from users where username = 'grein003'), '9DD05B17EAAE45259BB787D99377901F', SYSDATE, SYSDATE, 'Y', 'lee.greiner@duke.edu', 'Lee', null, null, null, 'Greiner', SYSDATE, null, '$2a$10$JNW.5B0oqjHxvdHatkgdkeItLH4L2awVeigR7vu1NgBUYOVYU6fo.', '9DD05B17EAAE45259BB787D99377901E', SYSDATE, 'UTC', 'grein003', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.user.User');

INSERT INTO USERS_TO_ROLES (USER_FK, ROLE_FK) VALUES ((select user_id from users where username = 'grein003'), (select role_id from role where name = 'Administrator'));
INSERT INTO USERS_TO_ROLES_AUD (user_fk, role_fk, revtype, rev) VALUES ((select user_id from users where username = 'grein003'), (select role_id from role where name = 'Administrator'), 0, hibernate_sequence.currval);
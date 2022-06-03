INSERT INTO PRIVILEGE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'View Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select privilege_id from privilege where name = 'View Users'), (select alternate_id from privilege where name = 'View Users'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'View Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO PRIVILEGE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Edit Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select privilege_id from privilege where name = 'Edit Users'), (select alternate_id from privilege where name = 'Edit Users'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Edit Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'User');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select role_id from role where name = 'User'), (select alternate_id from privilege where name = 'User'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'User', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Administrator');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select role_id from role where name = 'Administrator'), (select alternate_id from privilege where name = 'Administrator'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrator', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES ((select role_id from role where name = 'User'), (select privilege_id from privilege where name = 'View Users'));
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, (select role_id from role where name = 'User'), (select privilege_id from privilege where name = 'View Users'));
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES ((select role_id from role where name = 'Administrator'), (select privilege_id from privilege where name = 'View Users'));
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, (select role_id from role where name = 'Administrator'), (select privilege_id from privilege where name = 'View Users'));
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES ((select role_id from role where name = 'Administrator'), (select privilege_id from privilege where name = 'Edit Users'));
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, (select role_id from role where name = 'Administrator'), (select privilege_id from privilege where name = 'Edit Users'));
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');
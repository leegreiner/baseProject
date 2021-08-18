INSERT INTO PRIVILEGE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES ('42D42A6CE4A34B44A214F0ED9C43AF79', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'View Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select privilege_id from privilege where name = 'View Users'), '42D42A6CE4A34B44A214F0ED9C43AF79', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'View Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO PRIVILEGE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES ('D632420D186843F3BCD3E536D59E979B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Edit Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select privilege_id from privilege where name = 'Edit Users'), 'D632420D186843F3BCD3E536D59E979B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Edit Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES ('7D716E408FEB4D47A2A1ED17683B60F6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'User');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select role_id from role where name = 'User'), '7D716E408FEB4D47A2A1ED17683B60F6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'User', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLE (ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES ('EEF71F27DBE143268A8F9B7E9561755E', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Administrator');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES ((select role_id from role where name = 'Administrator'), 'EEF71F27DBE143268A8F9B7E9561755E', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrator', hibernate_sequence.currval, 0);
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
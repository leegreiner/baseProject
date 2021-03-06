INSERT INTO PRIVILEGE (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (privilege_seq.nextval, '42D42A6CE4A34B44A214F0ED9C43AF79', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'View Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES (privilege_seq.currval, '42D42A6CE4A34B44A214F0ED9C43AF79', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'View Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO PRIVILEGE (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (privilege_seq.nextval, 'D632420D186843F3BCD3E536D59E979B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Edit Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES (privilege_seq.currval, 'D632420D186843F3BCD3E536D59E979B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Edit Users', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLE (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (role_seq.nextval, '7D716E408FEB4D47A2A1ED17683B60F6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'User');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES (role_seq.currval, '7D716E408FEB4D47A2A1ED17683B60F6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'User', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLE (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (role_seq.nextval, 'EEF71F27DBE143268A8F9B7E9561755E', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Administrator');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, NAME, REV, REVTYPE) VALUES (role_seq.currval, 'EEF71F27DBE143268A8F9B7E9561755E', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrator', hibernate_sequence.currval, 0);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval, privilege_seq.currval - 1);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval, privilege_seq.currval);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select EXTRACT (EPOCH from CURRENT_TIMESTAMP()) * 1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');
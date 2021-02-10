INSERT INTO PRIVILEGE (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (privilege_seq.nextval, '42D42A6CE4A34B44A214F0ED9C43AF79', SYSDATE, SYSDATE, 0, 'View Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, REV, REVTYPE, NAME) VALUES (privilege_seq.currval, hibernate_sequence.currval, 0, 'View Users');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO PRIVILEGE (PRIVILEGE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (privilege_seq.nextval, 'D632420D186843F3BCD3E536D59E979B', SYSDATE, SYSDATE, 0, 'Edit Users');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO PRIVILEGE_AUD (PRIVILEGE_ID, REV, REVTYPE, NAME) VALUES (privilege_seq.currval, hibernate_sequence.currval, 0, 'Edit Users');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLE (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (role_seq.nextval, '7D716E408FEB4D47A2A1ED17683B60F6', SYSDATE, SYSDATE, 0, 'User');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, REV, REVTYPE, NAME) VALUES (role_seq.currval, hibernate_sequence.currval, 0, 'User');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLE (ROLE_ID, ALTERNATE_ID, CREATED_DATE, LAST_MODIFIED_DATE, VERSION, NAME) VALUES (role_seq.nextval, 'EEF71F27DBE143268A8F9B7E9561755E', SYSDATE, SYSDATE, 0, 'Administrator');
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLE_AUD (ROLE_ID, REV, REVTYPE, NAME) VALUES (role_seq.currval, hibernate_sequence.currval, 0, 'Administrator');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval, privilege_seq.currval - 1);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');

INSERT INTO ROLES_TO_PRIVILEGES (ROLE_FK, PRIVILEGE_FK) VALUES (role_seq.currval, privilege_seq.currval);
INSERT INTO AUDIT_REVISION_ENTITY (timestamp, initiator, user_id, id) select trunc(extract(day from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*86400+extract(hour from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*3600+extract(minute from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00'))*60+extract(second from (SYS_EXTRACT_UTC(systimestamp)-timestamp '1970-01-01 00:00:00 +00:00')))*1000, 'System', null, hibernate_sequence.nextval from dual;
INSERT INTO ROLES_TO_PRIVILEGES_AUD (REV, REVTYPE, ROLE_FK, PRIVILEGE_FK) VALUES (hibernate_sequence.currval, 0, role_seq.currval - 1, privilege_seq.currval - 1);
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Role');
INSERT INTO REVCHANGES (rev, entityname) values (hibernate_sequence.currval, 'edu.duke.rs.baseProject.role.Privilege');
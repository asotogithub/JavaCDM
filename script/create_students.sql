set echo on
spool create_students.log

drop role student;
create role student;
grant alter session to student;
grant create session to student;
grant create table to student;
grant create view to student;
grant create sequence to student;
grant create synonym to student;
grant create procedure to student; 
grant create trigger to student;
grant create type to student;
grant query rewrite to student;
grant create any index to student;
grant create public synonym to student;

grant execute on sys.dbms_stats to student;

create user student1 identified by student;
grant student to student1;
alter user student1 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student2 identified by student;
grant student to student2;
alter user student2 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student3 identified by student;
grant student to student3;
alter user student3 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student4 identified by student;
grant student to student4;
alter user student4 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student5 identified by student;
grant student to student5;
alter user student5 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student6 identified by student;
grant student to student6;
alter user student6 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student7 identified by student;
grant student to student7;
alter user student7 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student8 identified by student;
grant student to student8;
alter user student8 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student9 identified by student;
grant student to student9;
alter user student9 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student10 identified by student;
grant student to student10;
alter user student10 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student11 identified by student;
grant student to student11;
alter user student11 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student12 identified by student;
grant student to student12;
alter user student12 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student13 identified by student;
grant student to student13;
alter user student13 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student14 identified by student;
grant student to student14;
alter user student14 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student15 identified by student;
grant student to student15;
alter user student15 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student16 identified by student;
grant student to student16;
alter user student16 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student17 identified by student;
grant student to student17;
alter user student17 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student18 identified by student;
grant student to student18;
alter user student18 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student19 identified by student;
grant student to student19;
alter user student19 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user student20 identified by student;
grant student to student20;
alter user student20 default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

create user instructor identified by instructor;
grant student to instructor;
alter user instructor default tablespace user_data temporary tablespace temporary_data
quota unlimited on user_data;

spool off
set echo off




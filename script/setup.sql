rem
rem 	File:		Setup.sql
rem	Created: 	11-MAR-02  Monty Orme
rem	Called by:	
rem	Calls:		drop_students.sql which drops existing accounts student1-student20 and instructor.
rem			create_students.sql creates student1-student20 and instructor.
rem			create_tables.sql creates all objects for each account.
rem
@@drop_students
@@create_students
@@create_tables
set echo off
prompt 
prompt Setup script complete.
prompt
prompt Check drop_students.log for errors.
prompt
prompt Check create_students.log for errors.
prompt
prompt Check student1.log thru student20.log and instructor.log for errors.
prompt
connect / as sysdba
show user



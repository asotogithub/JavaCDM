REM   Script:   Hr_main.SQL
REM   Purpose:  To create users and initialise scripts that create schema objects.
REM   Created:  By Nagavalli Pataballa, on 16-MAR-2001 
REM		for the Introduction to Oracle9i: PL/SQL course.

REM   This script is invoked through CRESCHEM.SQL script.
REM   
REM   This script accepts 4 parameter - password for the accounts,
REM   name of default tablespace, name of temporary tablespace, 
REM   and password for user SYS 

@@hr_cre
@@hr_popul
@@hr_idx
@@hr_code
@@hr_comnt
@@del_data



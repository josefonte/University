drop database if exists userdb;
create database userDB; -- Creates the user Database
create user 'test'@'%' identified by '123456'; -- Creates the test user
grant all on userDB.* to 'test'@'%'; -- Gives all privileges to the new user on the newly created database
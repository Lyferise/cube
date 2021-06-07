create user 'test'@'localhost' identified by 'test';
grant all privileges on *.* to 'test'@'localhost';
flush privileges;
create database test;
grant all privileges on test.* to 'test'@'localhost';
use test;
create table if not exists integration (id int primary key, value int);
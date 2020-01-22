MYSQL Datenbank und Usereinstellungen

1. User erstellen
CREATE USER 'apache'@'localhost' IDENTIFIED BY 'admin';

2. Datenbank erstellen
CREATE DATABASE apache_db;

3. User Rechte auf Datenbank vergeben
GRANT ALL PRIVILEGES ON apache_db. * TO 'apache'@'localhost';

4. Tabelle User erstellen
CREATE TABLE t_User
(userid int primary key auto_increment
 name varchar(20),
 email varchar(30),
 password varchar(20),
 token varchar(40));

5. Tabelle Score erstellen
CREATE TABLE t_Score;
(score int,
 playerName varchar(20),
 timestamp int);
#!/bin/bash
# run in root directory

 
#setup mysql database

sudo mysql -e "CREATE DATABASE db_apache;"
sudo mysql -e "CREATE TABLE db_apache.t_user (pk_id integer primary key auto_increment, name varchar(20), email varchar(40), password varchar(32), token varchar(32));"
sudo mysql -e "CREATE TABLE db_apache.t_score (fk_user_id integer NOT NULL, user_name varchar(20), timestamp bigint, amount bigint, FOREIGN KEY (fk_user_id) REFERENCES db_apache.t_user(pk_id) ON DELETE CASCADE);"
sudo mysql -e "CREATE USER apache_user@localhost IDENTIFIED BY 'apache2';"
sudo mysql -e "GRANT ALL PRIVILEGES ON db_apache.* TO apache_user@localhost;"
sudo mysql -e "FLUSH PRIVILEGES;"


#PHP-FPM configuration

sudo a2enmod proxy_fcgi
sudo a2enconf php7.3-fpm
sudo systemctl restart apache2


#copy apache resources
sudo ./build.sh
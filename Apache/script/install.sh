#!/bin/bash
# run in root directory


#install required packages

sudo apt update
sudo apt install apache2
sudo apt install -y ufw apache2 mysql-server php-fpm bindfs
sudo apt install mysql-server
sudo mysql
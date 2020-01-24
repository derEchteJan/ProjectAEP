#Im Home-Verzeichnis des sudo-Users ausführen
#Notwendige System-Packages installieren



sudo apt update

udo apt install apache2
sudo apt install -y ufw apache2 mysql-server php-fpm bindfs
sudo apt install php7.3-mysql


sudo apt install mysql-server
sudo mysql
 
#neuen User anlegen 
sudo mysql -e "CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';"
sudo mysql -e "GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'localhost' WITH GRANT OPTION;"
sudo mysql -e "FLUSH PRIVILEGES;"

#PHP-FPM konfigurieren
sudo a2enmod proxy_fcgi
sudo a2enconf php7.3-fpm
sudo systemctl restart apache2


 

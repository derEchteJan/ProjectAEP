<?php
$servername = "localhost";
$username = "apache";
$password = "admin";

echo "servername: ".$servername."</br>";
echo "username: ".$username."</br>";
echo "password: ".$password."</br>";

echo "trying to connect..</br>";

try {
$conn = new PDO("mysql:host=$servername;dbname=apache_db", $username, $password);
$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
echo "connect success";
} catch(PDOException $e) {
echo "exception: ".$e->getMessage();
}

echo "Connected successfully";
?>

<?php
$servername = "localhost";
$username = "apache";
$password = "admin";

echo "servername: ".$servername."</br>";
echo "username: ".$username."</br>";
echo "password: ".$password."</br>";

echo "creating connection..</br>";

// Create connection
$conn = mysqli_connect($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
	die("connection failed: ".$conn->connect_error);
}
echo "Connected successfully<br>";

$statement = "SELECT * FROM apache_db.t_User";

echo "executing query</br>";

$result = $conn->query($statement);

echo "reading result</br>";

if ($result->num_rows > 0) {
	while($row = $result->fetch_assoc()) {
		echo "name: ".$row["name"];
	}
} else {
	echo "empty set</br>";
}

echo "closing connection</br>";

$conn->close();

?>

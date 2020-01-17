<?php

// MODELS

class User {
	public $name = "";
	public $email = "";
	public $password = "";
	public $token = "";
}

// FUNCTIONS

function parseRequest($body) {
	$result = new User();
	$json = json_decode($body, true);
	$result->name = $json['name'];
	$result->password = $json['password'];
	return $result;
}

function validateData(User $user) {
	if($user->name==null||$user->password==null) {
		http_response_code(400); 
		echo "400 Bad Request";
		exit(0);
	}
	$name_len = strlen($user->name);
	$pw_len = strlen($user->password);
	if($name_len<3||$name_len>30||$pw_len<3||$pw_len>30) {
		http_response_code(400); 
		echo "400 Bad Request";
		exit(0);
	}
}

function openMysqliConnection() {
	$servername = "localhost";
	$username = "apache";
	$password = "admin";
	$connection = mysqli_connect($servername, $username, $password);
	if($connection->connect_error) {
		http_response_code(500);
		echo "could not connect to database: ".$connection->connect_error;
		exit(0);
	}
	return $connection;
}

/*function generateToken() {
	return md5(rand()."");
}*/



// REQUEST HANDLING

$request_body = file_get_contents('php://input');
$user = parseRequest($request_body);
validateData($user);

// QUERY DATABASE

$db_conn = openMysqliConnection();

// check if username is taken
$sql_statement = "SELECT email, token FROM apache_db.t_User WHERE name = '".$user->name."' AND password = '".$user->password."'";
$sql_results = $db_conn->query($sql_statement);
if ($sql_results->num_rows == 1) {
	while($row = $result->fetch_assoc()) {
		$user->email = $row["email"];
		$user->token = $row["token"];
	}
} else {
	http_response_code(403);
	echo "403 Forbidden: Credentials invalid";
	exit(0);	
}

/*
$user->token = generateToken();
$sql_statement = "INSERT INTO apache_db.t_User (name, email, password, token) VALUES ('"
	.$user->name."', '"
	.$user->email."', '"
	.md5($user->password)."', '"
	.$user->token."')";
$db_conn->query($sql_statement);
*/
// DONE AND CLOSE

$db_conn->close();

// respond with new token
echo json_encode($user);

exit(0);

?>

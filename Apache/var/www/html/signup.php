<?php

// MODELS

class User {
	public $name = "";
	public $email = "";
	public $password = "";
	public $token = "";
}

class Token {
	public $token = "";
}

// FUNCTIONS

function parseRequest($body) {
	$result = new User();
	$json = json_decode($body, true);
	$result->name = $json['name'];
	$result->email = $json['email'];
	$result->password = $json['password'];
	return $result;
}

function validateData(User $user) {
	if($user->name==null||$user->email==null||$user->password==null) {
		http_response_code(400); 
		echo "400 Bad Request";
		exit(0);
	}
	$name_len = strlen($user->name);
	$email_len = strlen($user->email);
	$pw_len = strlen($user->password);
	if($name_len<3||$name_len>30||$email_len<3||$email_len>30||$pw_len<3||$pw_len>30) {
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

function generateToken() {
	return md5(rand()."");
}



// REQUEST HANDLING

$request_body = file_get_contents('php://input');
$user = parseRequest($request_body);
validateData($user);

// QUERY DATABASE

$db_conn = openMysqliConnection();

// check if username is taken
$sql_statement = "SELECT name FROM apache_db.t_User WHERE name = '".$user->name."'";
$sql_results = $db_conn->query($sql_statement);
if ($sql_results->num_rows >= 1) {
	http_response_code(409);
	echo "409 Conflict: Username already taken. Try login";
	exit(0);
}

// if not -> insert new user
$user->token = generateToken();
$sql_statement = "INSERT INTO apache_db.t_User (name, email, password, token) VALUES ('"
	.$user->name."', '"
	.$user->email."', '"
	.md5($user->password)."', '"
	.$user->token."')";
$db_conn->query($sql_statement);

// DONE AND CLOSE

$db_conn->close();

// respond with new token
$response_object = new Token();
$response_object->token = $user->token;
echo json_encode($response_object);

exit(0);

?>

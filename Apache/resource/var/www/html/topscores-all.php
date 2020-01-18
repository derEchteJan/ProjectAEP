<?php

// MODELS

$max_results = 10;

class User {
	public $id = "";
	public $token = "";
}

class Score {
	public $user_name = "";
	public $amount = "";
	public $timestamp = "";
}

// FUNCTIONS

function parseRequest($body) {
	$result = new User();
	$result->token = $_SERVER[HTTP_TOKEN];
	return $result;
}

function validateData(User $user) {
	if($user->token==null/* or no integer */) {
		http_response_code(400); 
		echo "400 Bad Request";
		exit(0);
	}
}

function openMysqliConnection() {
	$servername = "localhost";
	$username = "apache_user";
	$password = "apache2";
	$connection = mysqli_connect($servername, $username, $password);
	if($connection->connect_error) {
		http_response_code(500);
		echo "could not connect to database: ".$connection->connect_error;
		exit(0);
	}
	return $connection;
}


// REQUEST HANDLING

//$request_body = file_get_contents('php://input');
//$user = parseRequest($request_body);
//echo json_encode($user);
//validateData($user);

// QUERY DATABASE

$db_conn = openMysqliConnection();

/*
// check if token valid -> get user
$sql_statement = "SELECT pk_id FROM db_apache.t_user WHERE token = '".$user->token."'";
$sql_results = $db_conn->query($sql_statement);
if ($sql_results->num_rows > 0) {
	while($row = $sql_results->fetch_assoc()) {
		$user->id = $row["pk_id"];
	}
} else {
	http_response_code(403);
	echo "403 Forbidden: Credentials invalid";
	exit(0);
}*/

// get top scores
$sql_statement = "SELECT user_name, amount, timestamp FROM db_apache.t_score ORDER BY amount DESC LIMIT ".$max_results;
$sql_results = $db_conn->query($sql_statement);
$result_arr = array();
if($sql_results->num_rows > 0) {
	while($row = $sql_results->fetch_assoc()) {
		$rowScore = new Score();
		$rowScore->user_name = $row["user_name"];
		$rowScore->amount = $row["amount"];
		$rowScore->timestamp = $row["timestamp"];
		array_push($result_arr, $rowScore);
	}
} /*else {
	http_response_code(404);
	echo "404 Not Found: no scores found for user_id ".$user->id;
	exit(0);
};*/

// DONE AND CLOSE

$db_conn->close();

// respond with complete user
header('Content-Type: application/json');
echo json_encode($result_arr);

exit(0);

?>

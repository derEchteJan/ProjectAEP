<?php

// MODELS

class User {
	public $id = "";
	public $name = "";
	public $token = "";
}

class Score {
	public $user_id = "";
	public $user_name = "";
	public $amount = "";
	public $timestamp = "";
	public $token = "";
}

// FUNCTIONS

function parseRequest($body) {
	$result = new Score();
	$json = json_decode($body, true);
	$result->amount = $json['amount'];
	$result->timestamp = $json['timestamp'];
	$result->token = $_SERVER[HTTP_TOKEN];
	return $result;
}

function validateData(Score $score) {
	if($score->amount==null||$score->timestamp==null||$score->token==null) {
		http_response_code(400); 
		echo "400 Bad Request";
		exit(0);
	}
	// todo: input auf integer prüfen
	//var_dump($score-token);
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

$request_body = file_get_contents('php://input');
$score = parseRequest($request_body);
//echo json_encode($score);
validateData($score);

// QUERY DATABASE

$db_conn = openMysqliConnection();

// check if token valid -> get user
$sql_statement = "SELECT pk_id, name FROM db_apache.t_user WHERE token = '".$score->token."'";
$sql_results = $db_conn->query($sql_statement);
if ($sql_results->num_rows == 1) {
	while($row = $sql_results->fetch_assoc()) {
		$score->user_id = $row["pk_id"];
		$score->user_name = $row["name"];
	}
} else {
	http_response_code(403);
	echo "403 Forbidden: Credentials invalid";
	exit(0);
}
// insert the new score
$sql_statement = "INSERT INTO db_apache.t_score (fk_user_id, user_name, amount, timestamp) VALUES ("
	.$score->user_id.", '"
	.$score->user_name."', "
	.$score->amount.", "
	.$score->timestamp.")";
$sql_results = $db_conn->query($sql_statement);
if(mysqli_affected_rows($db_conn) < 1) {
	http_response_code(500);
	echo "500 Server Error: Database";
	exit(0);
};
// DONE AND CLOSE

$db_conn->close();

// respond with complete user
echo "create score: success";

exit(0);

?>

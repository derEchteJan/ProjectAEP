/*
 * scores.js
 * loads the highscores from the rest and displays them in the table of scores.html
 */

// MARK: - button action

function loadScores() {
	lockButton();
	var debugContainer = document.getElementById("debug_container");   // invisible debug html elemet to write debug output in
	// setup request object
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "/rest/topscores-all.php", true);
	xhr.onreadystatechange = processRequest;
	// set response handling
	function processRequest(e) {
		if (xhr.readyState == 4) {	// state 4 = ready -> time to partay!!!
			debugContainer.innerHTML = "Response-Code: "+(xhr.status).toString() + "</br>contents:</br>" + xhr.responseText;
			if (xhr.responseText.length == 0) {
				addNoResponseIndicator();
			} else {
				try {
					var responseObject = JSON.parse(xhr.responseText);
					displayScores(responseObject);
				} catch (exception) {
					debugContainer.innerHTML = "Error: " + exception;
					addErrorIndicator(exception);
				}
			}
			unlockButton();
		} else {
			debugContainer.innerHTML = "Error: Status: "+(xhr.status).toString();
			unlockButton();
		}
	}
	// send it
	xhr.send();
}

// MARK: error handling

function addNoResponseIndicator() {
	let container = document.getElementById("error_indicator_container");
	container.setAttribute("style", "");
	container.innerHTML = "Der Server hat nicht geantwortet";
}

function addErrorIndicator(error) {
	let container = document.getElementById("error_indicator_container");
	container.setAttribute("style", "color:red");
	container.innerHTML = "Es ist ein JavaScript Fehler aufgetreten:\n"+error;
}


// MARK: - display recieved data

function displayScores(json) {
	var table = document.getElementById("scores_table");
	clearTableContent(table);
	if (json.length > 0) {
		for (var i = 0; i < json.length; i++) {
			var newRow = table.insertRow(i+1);
			var userNameCell = newRow.insertCell(0);
			var scoreCell = newRow.insertCell(1);
			var dateCell = newRow.insertCell(2);
			userNameCell.innerHTML = json[i].user_name;
			scoreCell.innerHTML = json[i].amount;
			dateCell.innerHTML = parseDateFromTimestamp(json[i].timestamp);
		}
	} else {
		addEmptySetRowToTable(table);
	}
}

function clearTableContent(table) {
	for (var i = table.rows.length - 1; i > 0; i--) {
		table.deleteRow(i);
	}
}

function parseDateFromTimestamp(timestamp) {
	// convert to dd.mm.yyyy h:mm:ss
	// because js doesnt have fucking date format duh..
	var date = new Date(Number(timestamp));
	var day = date.getDate().toString();
	if (day.length == 1) day = "0" + day;
	var month = (date.getMonth()+1).toString();
	if (month.length == 1) month = "0" + month;
	var min = date.getMinutes().toString();
	if (min.length == 1) min = "0" + min;
	var sec = date.getSeconds().toString();
	if (sec.length == 1) sec = "0" + sec;
	return  day + "." + month + "." + date.getFullYear() + " - " + date.getHours() + ":" + min + ":" + sec + " h";
}

function addEmptySetRowToTable(table) {
	var newRow = table.insertRow(1);
	var cell = newRow.insertCell(0);
	newRow.insertCell(1);
	newRow.insertCell(2);
	cell.innerHTML = "Noch keine Bestleistungen vorhanden...";
}


// MARK: - refresh button control

function lockButton() {
	var button = document.getElementById("refresh_button");
	button.setAttribute("disabled", "");
	button.innerHTML = "Lade...";
}

function unlockButton() {
	var button = document.getElementById("refresh_button");
	button.removeAttribute("disabled");
	button.innerHTML = "Aktualisieren";
}

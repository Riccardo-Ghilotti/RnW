<%@ page language="java" contentType="text/html; charset=UTF-8" 
	isELIgnored="False"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Write - RnW</title>
</head>
<body>
	<div id="textBox">
		<h1>Titolo:</h1>
		<input type="text" id="title" value="${TITLE}"><br>
		<div id="writeBox"></div>
		<button onclick="saveText()">Salva!</button>
		<button id="nextButton" onclick="nextArea()">Vai alla prossima macro-sezione</button>
		<button id="prevButton" onclick="previousArea()"> Vai alla macro-sezione precedente </button>
		<button id="createSection" onclick="createSection()">Crea una nuova sezione</button>
	</div>
	
<script>
	var old_id = ${ID};
	var titolo = "${TITLE}";
	var intro = ${INTRO};
	var corpus = ${CORPUS};
	var conc = ${CONC};
	var currentArea = "intro";
	
	
	function textToVariable(){
		x = document.getElementsByClassName("currentBox");
		x = [x].map(x => x.values);
		if(x.length == 1)
			x = x[0];
		return x;
	}
	
	function fillArea(area){
		var writeBox = document.getElementById("writeBox");
		var value
		switch(area){
		case "intro":
			value = intro;
			break;
		case "corpus":
			value = corpus;
			break;
		case "conc":
			value = conc;
			break;
		}
		
		let elements = document.getElementsByClassName("currentBox");
		if(value == "" && elements.length === 0){
			var box = document.createElement("textarea");
			box.setAttribute("class", "currentBox");
			writeBox.appendChild(box);
		}
		else {
			for(section in value){
				var box = document.createElement("textarea");
				box.setAttribute("value", section);
				box.setAttribute("class", "currentBox");
				writeBox.appendChild(box);
			}
		}
	}
	
	function nextArea(){
		console.log(currentArea);
		console.log(intro);
		switch(currentArea){
		case "intro":
			intro = textToVariable();
			currentArea = "corpus";
			fillArea("corpus");
			document.getElementById("prevButton").disabled = false;
			document.getElementById("nextButton").disabled = false;
			break;
		case "corpus":
			corpus = textToVariable();
			currentArea="conc";
			fillArea("conc");
			document.getElementById("nextButton").disabled = true;
			break;
		}
	}
	
	function previousArea(){
		console.log(currentArea);
		switch(currentArea){	
		case "conc":
			conc = textToVariable();
			currentArea = "corpus";
			fillArea("corpus");
			document.getElementById("prevButton").disabled = false;
			document.getElementById("nextButton").disabled = false;
			break;
		case "corpus":
			corpus = textToVariable();
			currentArea="intro";
			fillArea("intro");
			document.getElementById("prevButton").disabled = true;
			break;
		}
	}
	
	
	function createSection(){
		var box = document.createElement("textarea");
		document.getElementById("writeBox").appendChild(box);
	}
	
	function saveText(){
		titolo = document.getElementById("title").value;
		switch(currentArea){
		case "intro":
			intro = textToVariable();
			break;
		case "corpus":
			corpus = textToVariable();
			break;
		case "conc":
			conc = textToVariable();
			break;
		}
		const xhttp = new XMLHttpRequest();
		xhttp.open("POST", "/RnW/saveText");
		xhttp.send(id, title, intro, corpus, conc);
		
	}
	
	window.onload = fillArea("intro");
	
</script>
</body>
</html>
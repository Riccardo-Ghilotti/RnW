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
	<form action="saveText" id="textBox" method="POST">
		<h1>Titolo:</h1>
		<input type="text" id="title" name="title" value="${TITLE}"><br>
		<div id="writeBox"></div>
		<button  type="button" id="prevButton" onclick="previousArea()" 
		disabled> Vai alla macro-sezione precedente </button>
		<button type="button" id="nextButton" 
		onclick="nextArea()">Vai alla prossima macro-sezione</button>
		<button type="button" id="createSectionButton" 
		onclick="createSection()">Crea una nuova sezione</button>
		<input type="submit" value="Salva!">
	</form>
	
<script>
	var id = "${ID}";
	var u_id = "${U_ID}";
	var titolo = "${TITLE}";
	var intro = ${INTRO};
	var corpus = ${CORPUS};
	var conc = ${CONC};
	var error = "${ERROR}";
	var currentArea = "intro";
	
	const form = document.getElementById("textBox");
	
	form.addEventListener("submit", function(event){
		if(saveText()){
			formEls= [];
			for(let i = 0; i < 5; i++){
				formEls[i] = document.createElement("input");
			}
			
			formEls[0].setAttribute("name", "intro");
			formEls[0].setAttribute("value", JSON.stringify(intro));
			formEls[1].setAttribute("name", "corpus");
			formEls[1].setAttribute("value", JSON.stringify(corpus));
			formEls[2].setAttribute("name","conc");
			formEls[2].setAttribute("value", JSON.stringify(conc));
			formEls[3].setAttribute("name","text_id");
			formEls[3].setAttribute("value", id);
			formEls[4].setAttribute("name","author");
			formEls[4].setAttribute("value", u_id);
			
			for(let i = 0; i < 5; i++)
				form.appendChild(formEls[i]);
			
			return true;
		}
		return false;
	});
	
	

	function createSection(){
		var box = document.createElement("textarea");
		box.setAttribute("class", "currentBox");
		document.getElementById("writeBox").appendChild(box);
	}
	
	function textToVariable(){
		objects = document.getElementsByClassName("currentBox");
		console.log(objects);
		y = [];
		for(let i = 0; i < objects.length; i+=0){
			y.push(objects[i].value);
			objects[i].parentNode.removeChild(objects[i]);
		}
		return y;
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
		if(value == "" && elements.length == 0){
			var box = document.createElement("textarea");
			box.setAttribute("class", "currentBox");
			writeBox.appendChild(box);
		}
		else {
			for(section in value){
				var box = document.createElement("textarea");
				box.value = value[section];
				box.setAttribute("class", "currentBox");
				writeBox.appendChild(box);
			}
		}
	}
	
	function nextArea(){
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
	
	
	function saveText(){
		titolo = document.getElementById("title").value;
		switch(currentArea){
		case "intro":
			intro = textToVariable();
			return true;
		case "corpus":
			corpus = textToVariable();
			return true;
		case "conc":
			conc = textToVariable();
			return true;
		}
		return false;
		}
	
	if(error != "Non sei l'autore di questo testo")
		window.onload = fillArea("intro");
	else{
		window.onload = function(){
			document.innterHTML="";
			alert(error);
			var link = document.createElement("a");
			link.innerHTML = "Ritorna alla home";
			link.setAttribute("href").value("localhost:8080/RnW/index.jsp")
		}
	}
	
</script>
</body>
</html>
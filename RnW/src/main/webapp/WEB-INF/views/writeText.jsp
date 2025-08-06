 <%@ page language="java" contentType="text/html; charset=UTF-8" 
	isELIgnored="False"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Write - RnW</title>
<style>
.col{
padding-top:2%}
</style>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">

</head>
<body>
<div class="container">
	<form action="saveText" id="textBox" method="POST" accept-charset="UTF-8">
		<h1>Titolo:</h1>
		<input type="text" id="title" name="title" value="${TITLE}" class="form-control"><br>
		<c:if test="${not empty TITLE}"> <script>document.getElementById("title").readonly="readonly"</script></c:if>
		<h4 id="sectionText"></h4>
		<div id="writeBox"></div>
		<div class="row">
		<div class="col">
		<button  type="button" id="prevButton" onclick="previousArea()" 
		disabled class="btn btn-outline-primary"> Vai alla macro-sezione precedente </button>
		</div>
		<div class="col">
		<button type="button" id="nextButton" 
		onclick="nextArea()" class="btn btn-outline-primary" >Vai alla prossima macro-sezione</button>
		</div>
		<div class="col">
		<button type="button" id="createSectionButton" 
		onclick="createSection()" class="btn btn-outline-primary">Crea una nuova sezione</button>
		</div>
		<div class="col">
		<input type="submit" value="Salva!" class="btn btn-success">
		</div>
		</div>
	</form>
	</div>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
	
<script>
	var id = "${ID}";
	var u_id = sessionStorage.getItem("userId");
	var titolo = "${TITLE}";
	var intro = ${INTRO};
	var corpus = ${CORPUS};
	var conc = ${CONC};
	var error = `${ERROR}`;
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
		box.setAttribute("class", "currentBox form-control");
		document.getElementById("writeBox").appendChild(box);
	}
	
	function textToVariable(){
		objects = document.getElementsByClassName("currentBox");
		console.log(objects);
		y = [];
		for(let i = 0; i < objects.length; i+=0){
			if(objects[i].value != "")
				y.push(objects[i].value);
			objects[i].parentNode.removeChild(objects[i]);
		}
		return y;
	}

	var sectionText = document.getElementById("sectionText");
	function fillArea(area){
		var writeBox = document.getElementById("writeBox");
		var value
		switch(area){
		case "intro":
			sectionText.innerHTML="Introduzione:";
			value = intro;
			break;
		case "corpus":
			sectionText.innerHTML="Svolgimento:";
			value = corpus;
			break;
		case "conc":
			sectionText.innerHTML="Conclusione:";
			value = conc;
			break;
		}
		
		let elements = document.getElementsByClassName("currentBox");
		if(value == "" && elements.length == 0){
			var box = document.createElement("textarea");
			box.setAttribute("class", "currentBox form-control");
			writeBox.appendChild(box);
		}
		else {
			for(section in value){
				var box = document.createElement("textarea");
				box.value = value[section];
				box.setAttribute("class", "currentBox form-control");
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
	
	if(error != ""){
		alert(error);
		window.onload = fillArea("intro");
	}
	else{
		window.onload = fillArea("intro");
	}
	
</script>
</body>
</html>
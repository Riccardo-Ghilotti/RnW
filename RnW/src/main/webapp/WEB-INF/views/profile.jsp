<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="False"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title id="profileName">Account - ${NAME}</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<style>
.col-6{
padding-top:3%;
padding-bottom:3%;}
.col-3{
padding-top:1.5%;
padding-bottom:1.5%;}
.col-sm-2{
padding-top:1%;
padding-bottom:1%;}
</style>
</head>
<body>
	<div class="container" style="padding-top:2%" id="mainContainer">
		<div class="row">
			
			<h1>Profilo - <span id = "nameHeader" style="display:inline;">${NAME}</span></h1>
		</div>
		
		<c:if test="${IS_ADMIN or IS_OWNER}">
		<div class="row">
			<c:if test="${IS_OWNER}">
			<div class="col-3">
				<form action="writeText" method="POST">
				<input type="text" value="${ID}" name="id" hidden="true">
				<input type="submit" value="Scrivi un Testo"
				class="btn btn-warning">
				</form>
			<script>
			sessionStorage.setItem("userId", "${ID}");
			</script>
			</div>
			</c:if>
			
			<c:if test="${IS_ADMIN}">
			<div class="col-3" align="center">
			<form action="adminView" method="POST">
				<input type="text" name="userId" hidden="true" 
				id="accessInput">
				<input type="submit" value="Accedi alla gestione degli utenti"
				class="btn btn-dark">
				</form>
				</div>
			</c:if>
			</div>
		</c:if>
			
			<div class="row">
			<c:if test="${IS_ADMIN or IS_OWNER}">
			
				<div class="col-3"  align="left">
					<button class="btn btn-danger" onclick="deleteAccount()">Cancella l'account!</button>
				</div>
				
				<div class="col-3"  align="center">
					<button class="btn btn-success" onclick="changeSetting(0)">Cambia nome!</button>
				</div>
				
				
			</c:if>
			<c:if test="${IS_OWNER}">
				<div class="col-3" align="center">
					<button class="btn btn-success" onclick="changeSetting(1)"> Cambia password!</button>
				</div>
			
			</c:if>
			<div class="row">
			</div>
				<h3>Elenco dei testi creati dall'utente:</h3>
			</div>
			
			<div class="row" id ="linkBox" style="position:fixed; bottom:0%; left:20%; right:20%;">
			<div class="col-6" align="center">
				<a href="home" class="btn btn-info" role="button">Vai alla home!</a>
			</div>
			<div class="col-6" align="center">
				<a href="login" class="btn btn-info" role="button">Vai al login!</a>
			</div>
		</div>
	</div>
	</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>
	var texts = ${TEXTS};

	
	
	function createForm(id, u_id){
		form = document.createElement("form");
		form.method = "POST";
		form.action = "/RnW/text";
		form.style.display = "inline";
		
		var input = document.createElement("input");
		input.setAttribute("hidden","true");
		input.setAttribute("value", id);
		input.setAttribute("name", "textId");
		form.appendChild(input);
		
		var input2 = document.createElement("input");
		input2.setAttribute("hidden","true");
		input2.setAttribute("value", u_id);
		input2.setAttribute("name", "userId");
		form.appendChild(input2);
		
		return form;
	}
	
	window.onload = function(){
		var box = document.createElement("div");
		document.getElementById("mainContainer").insertBefore(box, document.getElementById("linkBox"));
		box.setAttribute("id","textbox");
		box.setAttribute("class", "row align-items-center")
		if(texts == null){
			box.innerHTML = "Non è ancora stato scritto nessun testo";
			}
		else {
			for(let i = 0; i < texts.length; i++){
				var singleTextBox = document.createElement("div");
				singleTextBox.setAttribute("class", "col-sm-2");
				singleTextBox.setAttribute("align", "center");
				var titleButton = document.createElement("button");
				var form = createForm(texts[i]["id"], sessionStorage.getItem("userId"));
				titleButton.innerHTML = texts[i]["title"];
				titleButton.setAttribute("type", "submit");
				form.appendChild(titleButton);
				singleTextBox.appendChild(form);
				box.appendChild(singleTextBox);
			}
		}
		
		var accessInput = document.getElementById("accessInput");
		if(accessInput){
			accessInput.setAttribute("value", sessionStorage.getItem("userId"));
		}
	}
	
	function changeSetting(setting){
		const xhttp = new XMLHttpRequest();
		var newName = "${NAME}";
		xhttp.onreadystatechange = function(){
			if(this.readyState == 4){
				alert(xhttp.responseText);
				}
			if(this.readyState == 4 && this.status == 200){
				document.getElementById("nameHeader").innerHTML = newName;
				document.getElementById("profileName").innerHTML = "Account - " + newName;
			}
		}
		switch(setting){
		case 0:
			newName = prompt("Quale sarà il tuo nuovo nome?");
			xhttp.open("POST", "/RnW/changeName")
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
		  	xhttp.send("userId=${ID}" 
		  			+"&newName=" + newName);
		  	break;
		case 1:
			var pwd = prompt("Qual'è la tua vecchia password?");
			var newPwd = prompt("Quale sarà la tua nuova password?");
			
			xhttp.open("POST", "/RnW/changePwd")
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
	  		xhttp.send("userId=${ID}&pwd=" + pwd 
	  				+ "&newPwd=" + newPwd);
			break;
		}
	}
	
	
	function deleteAccount(){
		const xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			if(this.readyState == 4){
				alert(xhttp.responseText);
				}
			if(this.readyState == 4 && this.status == 200){
				sessionStorage.removeItem("userId")
				document.location.href = "/RnW/index.jsp";
			}
		}
		
		xhttp.open("POST", "/RnW/deleteUser")
		xhttp.setRequestHeader("Content-type", 
				"application/x-www-form-urlencoded");
  		xhttp.send("userId=${ID}");
	}
	
</script>
</html>
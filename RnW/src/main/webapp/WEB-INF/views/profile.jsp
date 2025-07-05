<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="False"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Your Account - ${NAME}</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">

</head>
<body>
	<h1>${NAME}</h1>
</body>

	<c:if test="${IS_OWNER}">
		<form action="writeText" method="POST">
		<input type="text" value="${ID}" name="id" hidden="true">
		<input type="submit" value="Scrivi un Testo">
		</form>
	<script>
	sessionStorage.setItem("userId", "${ID}");
	</script>
	</c:if>
	
	<c:if test="${IS_ADMIN}">
	<form action="adminView" method="POST">
		<input type="text" name="userId" hidden="true" 
		id="accessInput">
		<input type="submit" value="Accedi alla gestione degli utenti">
		</form>
	</c:if>
	
	
	<c:if test="${IS_ADMIN or IS_OWNER}">
	<button onclick="changeSetting(0)">Cambia nome!</button>
	<button onclick="changeSetting(1)"> Cambia password!</button>
	</c:if>
	
	
	
	<a href="home" class="btn btn-info" role="button">Vai alla home!</a>
	
	<a href="login" class="btn btn-info" role="button">Vai al login!</a>
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
		document.body.appendChild(box);
		box.setAttribute("id","textbox");
		if(texts == null){
			box.innerHTML = "Non hai ancora scritto nessun testo";
			}
		else {
			console.log(texts);
			for(let i = 0; i < texts.length; i++){
				var titleButton = document.createElement("button");
				var form = createForm(texts[i]["id"], "${ID}");
				titleButton.innerHTML = texts[i]["title"];
				titleButton.setAttribute("type", "submit");
				form.appendChild(titleButton);
				box.appendChild(form);
			}
		}
		
		var accessInput = document.getElementById("accessInput");
		if(accessInput){
			accessInput.setAttribute("value", sessionStorage.getItem("userId"));
		}
	}
	
	function changeSetting(setting){
		const xhttp = new XMLHttpRequest();
		
		xhttp.onreadystatechange = function(){
			if(this.readyState == 4){
				alert(xhttp.responseText);
				}
		}
		switch(setting){
		case 0:
			var newName = prompt("Quale sarà il tuo nuovo nome?");
			xhttp.open("POST", "/RnW/changeName")
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
		  	xhttp.send("userId=" + sessionStorage.getItem("userId") 
		  			+"&newName=" + newName);
		  	location.reload();
		  	break;
		case 1:
			var pwd = prompt("Qual'è la tua vecchia password?");
			var newPwd = prompt("Quale sarà la tua nuova password?");
			
			xhttp.open("POST", "/RnW/changePwd")
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
	  		xhttp.send("userId="+ sessionStorage.getItem("userId") + "&pwd=" + pwd 
	  				+ "&newPwd=" + newPwd);
			break;
		}
	}
	
	
</script>
</html>
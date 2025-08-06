<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Home - RnW</title>
<style>
.col-6{
padding-bottom:2%;}</style>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>
<div class="container" id="bodyContainer" style="padding-top:5%">
	<h1>Ecco dei testi da leggere:</h1>
	<div class="row"id="box" style="padding:2%"></div>
	
	<div class="row"  style="position:fixed; bottom:0%; left:20%; right:20%; background-color:white">
	<div class="col-6" id="profileButton" align="center"></div></div>
</div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script>

	var texts = ${TEXTS};
	var u_id = sessionStorage.getItem("userId");
	var error = "${ERROR}";
	
	if(error != ""){
		alert(error);
	}
	
	
	function createForm(params, link){
		form = document.createElement("form");
		form.method = "POST";
		form.action = link;
		form.style.display = "inline";
		
		for(i = 0; i < params.length; i++){
			var input = document.createElement("input");
			input.setAttribute("hidden","true");
			input.setAttribute("value", params[i]["value"]);
			input.setAttribute("name", params[i]["name"]);
			form.appendChild(input);
		
		}
		
		return form;
	}
	
	window.onload = function(){
		if(texts != null){
			for(let i = 0; i < texts.length; i++){
				var divText = document.createElement("div");
				divText.setAttribute("class", "col-3");
				var titleButton = document.createElement("button");
				var params = [{"name":"textId", "value": texts[i]["id"]},
							{"name":"userId", "value": u_id}];
				var form = createForm(params, "/RnW/text");
				titleButton.innerHTML = texts[i]["title"];
				titleButton.setAttribute("type", "submit");
				form.appendChild(titleButton);
				divText.append(form)
				box.appendChild(divText);
				}
		}
		else
			document.getElementById("box").innerHTML = "Nessun testo è ancora stato scritto";
		
		console.log(u_id);
		
		if(u_id != null){
			var params = [{"name": "userId", "value":u_id},
				{"name":"ownerId", "value":u_id}];
			var form = createForm(params, "/RnW/user");
			var profileButton = document.createElement("button");
			profileButton.innerHTML = "Torna al profilo";
			profileButton.setAttribute("type", "submit");
			profileButton.setAttribute("class", "btn btn-info");
			form.appendChild(profileButton);
			document.getElementById("profileButton").appendChild(form);
		}
		else{
			var loginButton = document.createElement("a");
			loginButton.innerHTML = "Vai al login";
			loginButton.setAttribute("href", "/RnW/login");
			loginButton.setAttribute("class", "btn btn-info");
			document.getElementById("profileButton").appendChild(loginButton);
		}
		}
</script>
</html>
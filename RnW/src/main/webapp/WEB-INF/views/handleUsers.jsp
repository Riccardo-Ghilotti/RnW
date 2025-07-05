<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestione degli utenti - RnW</title>
</head>
<body>
<h1>SEGNALAZIONI:</h1>
	<div id="reportsBox"></div>

<h1>UTENTI:</h1>
	<div id="usersBox"></div>
	
	<form action="user" method="POST" accept-charset="UTF-8">
		<input type="text" value="${ID}" name="userId" hidden="true">
		<input type="text" value="${ID}" name="ownerId" hidden="true">
		<input type="submit" value="Vai al profilo">
	</form>
	
	<a href="home" class="btn btn-info" role="button">Vai alla home!</a>
</body>
<script>
	var users = ${USERS};
	var reports = ${REPORTS};
	console.log(reports);
	
	const reportsBox = document.getElementById("reportsBox");
	const usersBox = document.getElementById("usersBox");
	
	
	

	function createForm(params, link){
		var form = document.createElement("form");
		form.method = "POST";
		form.action = link;
		form.style.display = "inline";
		
		for(let j = 0; j < params.length; j++){
			var input = document.createElement("input");
			input.setAttribute("hidden","true");
			input.setAttribute("value", params[j]["value"]);
			input.setAttribute("name", params[j]["name"]);
			form.appendChild(input);
		
		}
		
		return form;
	}
	
	
	window.onload = function(){
		for(i = 0; i < users.length; i++){
			var div = document.createElement("div");
			div.setAttribute("class", "userBox");
			
			var userButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": users[i][0]}]
			
			var form = createForm(params, "/RnW/user");
			userButton.innerHTML = users[i][1];
			userButton.setAttribute("type", "submit");
			form.appendChild(userButton);
			div.appendChild(form);
			
			var button = document.createElement("button");
			button.setAttribute("onclick", "deleteUser(\"" + users[i][0] + "\")");
			button.innerHTML = "delete account";
			div.appendChild(button);
			
			var buttonCN = document.createElement("button");
			buttonCN.setAttribute("onclick", "changeName(\"" + users[i][0] + "\")");
			buttonCN.innerHTML = "cambia nome dell'account";
			div.appendChild(buttonCN);
			
			usersBox.appendChild(div);
		}
		
		for(i = 0; i < reports.length; i++){
			var div = document.createElement("div");
			div.setAttribute("class", "reportBox");
			
			
			var textButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "textId", "value": reports[i][0]}]
			
			var formText = createForm(params, "/RnW/text");
			textButton.innerHTML = reports[i][1];
			textButton.setAttribute("type", "submit");
			formText.appendChild(textButton);
			div.appendChild(formText);
			
			var creatorButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": reports[i][2]}]
			
			var formCreator = createForm(params, "/RnW/user");
			creatorButton.innerHTML = reports[i][3];
			creatorButton.setAttribute("type", "submit");
			formCreator.appendChild(creatorButton);
			div.appendChild(formCreator);
			
			var report = document.createElement("p");
			report.innerHTML = reports[i][6];
			div.append(report);
			
			var reporterButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": reports[i][4]}]
			
			var formReporter = createForm(params, "/RnW/user");
			reporterButton.innerHTML = reports[i][5];
			reporterButton.setAttribute("type", "submit");
			formReporter.appendChild(reporterButton);
			div.appendChild(formReporter);
			
			div.appendChild(formText);
			
			var button = document.createElement("button");
			button.setAttribute("onclick", "resolveReport(\"" + reports[i][7] + "\")");
			button.innerHTML = "risolvi segnalazione";
			div.appendChild(button);
			
			reportsBox.append(div);
		}
	}
	
	function deleteUser(userId){
	  if(userId == sessionStorage.getItem("userId")){
		alert("Non puoi cancellare il tuo account da questa pagina");
	  }
	  else{
		  
  	  var temp = confirm("Sicuro di voler cancellare l'account?");
	  if(temp){
		  const xhttp = new XMLHttpRequest();
		  xhttp.onreadystatechange= function(){
			  if(this.readyState == 4 && this.status == 200){
				  alert(this.responseText);
				  location.reload();
			  }
		  }
		  
		  xhttp.open("POST", "/RnW/deleteUser");
		  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		  xhttp.send("userId=" + userId);
	  		}
	  
	  	}		
	  }
	  
	  
	  function changeName(userId){
			const xhttp = new XMLHttpRequest();
			
			var newName = prompt("Inserire il nuovo nome dell'utente");
			xhttp.open("POST", "/RnW/changeName")
			
			xhttp.onreadystatechange = function(){
				if(this.readyState == 4){
					alert(this.responseText);
				  	location.reload();
					}
				else if (this.readyState == 4 && this.status != 200)
					alert("qualcosa è andato storto " + xhttp.responseText);
			}
		
			
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
		  	xhttp.send("userId=" + userId + "&newName=" + newName);

		
	  }
	  
	  
	  function resolveReport(reportId){
			const xhttp = new XMLHttpRequest();
			
			xhttp.open("POST", "/RnW/resolveReport")
			
			xhttp.onreadystatechange = function(){
				if(this.readyState == 4 && this.status == 200){
					alert(this.responseText);
				  	location.reload();
					}
				else if (this.readyState == 4 && this.status != 200)
					alert("qualcosa è andato storto " + this.resposeText);
			}
		
			
			xhttp.setRequestHeader("Content-type", 
					"application/x-www-form-urlencoded");
		  	xhttp.send("reportId=" + reportId);

		
	  }


	  
</script>
</html>
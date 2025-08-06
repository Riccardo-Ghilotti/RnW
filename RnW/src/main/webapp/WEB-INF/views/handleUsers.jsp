<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestione degli utenti - RnW</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<style>
.col-6{
padding-top:2%;
padding-bottom:2%;}

.col{
padding-top:1%;
padding-bottom:1%;}

.reportBox{
padding-top:1%;
padding-bottom:1%;
margin-bottom: 30px;
border-bottom: 1px solid #dee2e6;
}

.reportBox button {
margin: 8px 5px;
}

.reportBox p {
margin: 15px 0;
}

.reportBox form {
display: inline-block;
margin: 8px 5px;
}

.container{
padding-top:2%;
}
</style>
</head>
<body>
<div class="container" id="bodyContainer" style="padding:5%">
<h1>SEGNALAZIONI:</h1>
	<div id="reportsBox"></div>

<h1>UTENTI:</h1>
	<div id="usersBox"></div>
	
	
	</div>
	<div class="container">
	<div class="row" style="position:fixed; bottom:0%; left:20%; right:20%; background-color:white" align="center">
	<div class="col-6">
	<a href="home" class="btn btn-info" role="button">Vai alla home!</a>
	</div>
	<div class="col-6" id="linkToProfile">

	</div>
	</div>
	</div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>
	var users = ${USERS};
	var reports = ${REPORTS};
	var u_id = sessionStorage.getItem("userId");
	
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
			div.setAttribute("class", "userBox row");
			
			var buttonDiv = document.createElement("div");
			buttonDiv.setAttribute("class", "col");
			var userButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": users[i][0]}]
			
			var form = createForm(params, "/RnW/user");
			userButton.innerHTML = users[i][1];
			userButton.setAttribute("type", "submit");
			form.appendChild(userButton);
			buttonDiv.append(form);
			div.appendChild(buttonDiv);
			
			var buttonDiv = document.createElement("div");
			buttonDiv.setAttribute("class", "col");
			var button = document.createElement("button");
			button.setAttribute("onclick", "deleteUser(\"" + users[i][0] + "\")");
			button.setAttribute("class", "btn btn-danger")
			button.innerHTML = "cancella profilo";
			buttonDiv.append(button);
			div.appendChild(buttonDiv);
			
			
			var buttonDivCN = document.createElement("div");
			buttonDivCN.setAttribute("class", "col");
			var buttonCN = document.createElement("button");
			buttonCN.setAttribute("onclick", "changeName(\"" + users[i][0] + "\")");
			buttonCN.innerHTML = "cambia nome del profilo";
			buttonCN.setAttribute("class", "btn btn-warning")
			buttonDivCN.append(buttonCN);
			div.appendChild(buttonDivCN);
			
			
			usersBox.appendChild(div);
		}
		
		for(i = 0; i < reports.length; i++){
			var div = document.createElement("div");
			div.setAttribute("class", "reportBox row")
			
			var textButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "textId", "value": reports[i][0]}]
			
			var formText = createForm(params, "/RnW/text");
			textButton.innerHTML = "Testo segnalato: \n" + reports[i][1];
			textButton.setAttribute("class", "btn btn-warning");
			textButton.setAttribute("type", "submit");
			formText.appendChild(textButton);
			div.appendChild(formText);
			
			var creatorButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": reports[i][2]}]
			
			var formCreator = createForm(params, "/RnW/user");
			creatorButton.innerHTML = "Autore del testo: " + reports[i][3];
			creatorButton.setAttribute("type", "submit");
			creatorButton.setAttribute("class", "btn btn-warning");
			formCreator.appendChild(creatorButton);
			div.appendChild(formCreator);
			
			var report = document.createElement("p");
			report.innerHTML = "Commento lasciato con la segnalazione: " + reports[i][6];
			div.append(report);
			
			var reporterButton = document.createElement("button");
			var params = [{"name": "userId",
							"value": sessionStorage.getItem("userId")},
					{"name": "ownerId", "value": reports[i][4]}]
			
			var formReporter = createForm(params, "/RnW/user");
			reporterButton.innerHTML = "Utente che ha fatto la segnalazione: " + reports[i][5];
			reporterButton.setAttribute("type", "submit");
			reporterButton.setAttribute("class", "btn btn-warning");
			formReporter.appendChild(reporterButton);
			div.appendChild(formReporter);
			
			div.appendChild(formText);
			
			var button = document.createElement("button");
			button.setAttribute("class", "btn btn-info");
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

	  if(u_id != null){
			var params = [{"name": "userId", "value":u_id},
				{"name":"ownerId", "value":u_id}];
			var form = createForm(params, "/RnW/user");
			var profileButton = document.createElement("button");
			profileButton.innerHTML = "Torna al profilo";
			profileButton.setAttribute("type", "submit");
			profileButton.setAttribute("class", "btn btn-info");
			form.appendChild(profileButton);
			document.getElementById("linkToProfile").appendChild(form);
		}
	  
</script>
</html>
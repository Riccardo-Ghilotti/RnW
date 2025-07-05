<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Home - RnW</title>
</head>
<body>
	<h1>Ecco dei testi da leggere:</h1>
	<div id="box"></div>
	
	

</body>
<script>

	var texts = ${TEXTS};
	var u_id = sessionStorage.getItem("userId");
	var error = ${ERROR};
	
	switch(error){
	case 0:
		break;
	case 1:
		alert("Non puoi cancellare il testo");
		break;
	case 2:
		alert("Non puoi accedere a quest'area");
		break;
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
		if(texts != "Nessun testo è ancora stato scritto"){
			for(let i = 0; i < texts.length; i++){
				var titleButton = document.createElement("button");
				var params = [{"name":"textId", "value": texts[i]["id"]},
							{"name":"userId", "value": u_id}];
				var form = createForm(params, "/RnW/text");
				titleButton.innerHTML = texts[i]["title"];
				titleButton.setAttribute("type", "submit");
				form.appendChild(titleButton);
				box.appendChild(form);
				}
		}
		else
			document.getElementById("box").innerHTML = texts;
		
		console.log(u_id);
		
		if(u_id != null){
			var params = [{"name": "userId", "value":u_id},
				{"name":"ownerId", "value":u_id}];
			var form = createForm(params, "/RnW/user");
			var profileButton = document.createElement("button");
			profileButton.innerHTML = "Torna al profilo";
			profileButton.setAttribute("type", "submit");
			form.appendChild(profileButton);
			document.body.appendChild(form);
		}
		}
</script>
</html>
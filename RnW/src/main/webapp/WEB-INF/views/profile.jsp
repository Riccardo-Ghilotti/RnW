<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="False"
    pageEncoding="UTF-8"%>
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
	
	<form action="writeText" method="POST">
	<input type="text" value="${ID}" name="id" hidden="true">
	<input type="submit" value="Scrivi un Testo">
	</form>
	

	
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>
	var texts = '${TEXTS}';
	var sessionData = sessionStorage;
	
	
	function createForm(id, u_id){
		form = document.createElement("form");
		form.method = "POST";
		form.action = "/RnW/text";
		form.style.display = "inline";
		
		var input = document.createElement("input");
		input.setAttribute("hidden","true");
		input.setAttribute("value", id);
		input.setAttribute("name", "id");
		form.appendChild(input);
		
		var input2 = document.createElement("input");
		input2.setAttribute("hidden","true");
		input2.setAttribute("value", u_id);
		input2.setAttribute("name", "uid");
		form.appendChild(input2);
		
		return form;
	}
	
	window.onload = function(){
		var box = document.createElement("p");
		document.body.appendChild(box);
		box.setAttribute("id","textbox");
		if(typeof texts == "Non hai ancora scritto nessun testo"){
			box.innerHTML = texts;
			}
		else {
			texts = JSON.parse(texts);
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
	}
</script>
</html>
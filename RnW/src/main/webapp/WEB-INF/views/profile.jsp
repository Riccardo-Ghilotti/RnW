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
	<input type="text" value="${NAME}" name="username" hidden="true">
	<input type="submit" value="Scrivi un Testo">
	</form>
	

	
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>
	var texts = '${TEXTS}';
	
	window.onload = function(){
		var box = document.createElement("p");
		document.body.appendChild(box);
		box.setAttribute("id","textbox");
		if(typeof texts == "Non hai ancora scritto nessun testo"){
			box.innerHTML = texts;
			}
		else {
			texts = JSON.parse(texts);
			for(let i = 0; i < texts.length; i++){
				var titleButton = document.createElement("button");
				titleButton.setAttribute("href", "/RnW/text?id=" + texts[i][0]);
				titleButton.innerHTML = texts[i][1];
				box.appendChild(titleButton);
			}
		}
	}
</script>
</html>
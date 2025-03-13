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
	<input type="submit" value="Scrivi un Testo">
	</form>
	

	
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>	
	var texts = ${TEXTS};
	
	window.onload = function(){
		var box = document.createElement("p");
		document.body.appendChild(box);
		box.setAttribute("id","textbox");
		if(typeof texts === "string"){
			box.innerHTML = texts;
			}
		else {
			for(text in texts){
				var titleButton = document.createElement("a");
				titleButton.setAttribute("href", "/RnW/text?id=" + text[0]);
				titleButton.setAttribute("value", text[1]);
				titleButton.setAttribute("class", "btn btn-btn-light");
				box.appendChild(titleButton);
			}
		}
	}
</script>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${TITLE} - RnW</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">

<style type="text/css">
div {
	white-space: pre-wrap;
}
.col-5{
	padding-top:2%;
	padding-bottom:2%;
}
.control-form{
	height:50%
}
.button-form{
position:absolute; bottom:3%; right:1%; left:1%
}
.button-link{
position:absolute; bottom:3%; right:1%; left:1%
}
</style>
</head>
<body>
	<div id="textBox" class="container">
	</div>
	<div class="container">
		<div class="row">
		<c:if test="${IS_AUTHOR}">
			<div class="col-3">
		        <form method="POST" action="/RnW/writeText" class="control-form" style="position:relative">
	            <input type="hidden" name="userId" value="${U_ID}">
	            <input type="hidden" name="textId" value="${ID}">
	            <input type="submit" value="Modifica" class="btn btn-outline-success button-form">
	        </form>
	        </div>
	     </c:if>
	     <c:if test = "${IS_AUTHOR or IS_ADMIN}">  
	     	<div class="col-3">
	        <form method="POST" action="/RnW/deleteText" class="control-form" style="position:relative">
	            <input type="hidden" name="id" value="${U_ID}">
	            <input type="hidden" name="textId" value="${ID}">
	            <input type="submit" value="Elimina" class="btn btn-outline-danger button-form">
	        </form>
   	        </div>
   	        <div class="col-3">
   	        <div class="control-form" style="position:relative">
	        <button type="submit"  value="" class="btn btn-outline-primary button-form"
	            onclick="changeVisibility()" id="visibilityButton"></button>
	    	</div>
	    	</div>
	    </c:if>
	    </div>
   
    
    <c:if test="${U_ID != null}">
    	<div class="row align-items-start">
    	<button onclick="report()" class="btn btn-secondary">Segnala Testo</button>
    </div>
    </c:if>
	<div class="row">
	 <div class="col-3" id="authorButton">
	 </div>
 	</div>
	
	<c:if test="${U_ID != null}">
		<div class="row">
			<h1>Commenti:</h1>
    
  			<div class="form-group" style="position:relative">
  			<label for="commentContent" >Inserisci il tuo commento:</label>
   		 	<input type="text" name="content" id="commentContent" class="form-control" style="position:absolute; left:5%; right:15%">
   		 	<button onclick="addComment()" 
    			class="btn btn-primary" style="position:absolute; top:100%">Commenta! </button>
 	  </div>
	</div>
  	
    </c:if>
    
    <div id="commentBox" class="row">
   	
    
    </div>
    
    
    <form action="user" method="POST">
		<input type="hidden" value="${U_ID}" name="userId">
		<input type="hidden" value="${U_ID}" name="ownerId">
	</div>
	<div class="row"  style="position:fixed; bottom:0%; left:20%; right:20%;" >
	
		<div class="col-6"  align="center" style="position:relative;">
			<c:if test="${U_ID != null}">
			<input type="submit" class="btn btn-info button-link" value="Vai al profilo">
			</c:if>
			</form>
		</div>
	
	
			<div class="col-6" align="center" style="position:relative;">
			<a href="home" class="btn btn-info button-link" role="button">Vai alla home!</a>
			</div>
 	</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<script>
	var title = "${TITLE}";
	var intro = ${INTRO};
	var corpus = ${CORPUS};
	var conc = ${CONC};
	var comments = ${COMMENTS};
	var visibility = ${IS_PRIVATE};
	var sessionData = sessionStorage;
	
	const textBox = document.getElementById("textBox");
	const commentBox = document.getElementById("commentBox");
	var tmpHeader = null;
	var tmpHeaderBox = null;
	
	function changeVisibilityButton(){
		if(!visibility){
			var visButton = document.getElementById("visibilityButton");
			visButton.innerHTML = "Rendi Privato";
			visibility = !visibility;
		}
		else{
			var visButton = document.getElementById("visibilityButton");
			visButton.innerHTML = "Rendi Pubblico";
			visibility = !visibility;
		}
	}
	
	
	function changeVisibility(){	
		  const xhttp = new XMLHttpRequest();
		  xhttp.onreadystatechange = function(){
			  if(this.readyState == 4 && this.status == 200){
					alert(this.responseText);
					changeVisibilityButton();
			  }
			  else if(this.readyState == 4 && this.status != 200)
			  		alert(this.responseText);
			  }
		  xhttp.open("POST", "/RnW/changeVisibility");
		  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		  xhttp.send("userId=" + sessionData.getItem("userId") + 
				  "&textId=${ID}");
		  
	  }
	
	
	
	function printComment(commentId, authorName, commentContent){
		var cBox = document.createElement("div");
		
		var nameBox = document.createElement("form");
		nameBox.setAttribute("style", "margin-top:2%")
		nameBox.setAttribute("action","/RnW/userComment");
		var inputUserId = document.createElement("input");
		inputUserId.setAttribute("type", "hidden");
		inputUserId.setAttribute("name", "userId");
		inputUserId.setAttribute("value", sessionStorage.getItem("userId"));
		nameBox.append(inputUserId);
		
		
		var inputCommId = document.createElement("input");
		inputCommId.setAttribute("type", "hidden");
		inputCommId.setAttribute("name", "commentId");
		inputCommId.setAttribute("value", commentId);
		nameBox.append(inputCommId);
		
		var inputTextId = document.createElement("input");
		inputTextId.setAttribute("type", "hidden");
		inputTextId.setAttribute("name", "textId");
		inputTextId.setAttribute("value", "${ID}");
		nameBox.append(inputTextId);
		
		var buttonName = document.createElement("input");
		buttonName.setAttribute("type","submit");
		buttonName.setAttribute("value", authorName);
		nameBox.append(buttonName);
		
		cBox.append(nameBox);
		console.log(cBox);
		
		var contentBox = document.createElement("p");
		contentBox.innerHTML = "Commenta: " + commentContent;
		cBox.append(contentBox);
		
		 <c:if test="${IS_ADMIN or IS_AUTHOR}">
		var buttonDelete = document.createElement("button");
		buttonDelete.innerHTML = "Cancella Commento";
		buttonDelete.setAttribute("class", "btn btn-danger");
		buttonDelete.setAttribute("onclick", "deleteComment(\"" 
				+ commentId + "\")");
		cBox.append(buttonDelete);
		</c:if>
		
		commentBox.append(cBox);
	}
	
	function printComments(){
		for (i = 0; i < comments.length; i++){
			printComment(comments[i][0], comments[i][1], comments[i][2]);
		}
	}
	
	window.onload = function(){
		var visButton = document.getElementById("visibilityButton");
		console.log(visButton);
		if(sessionData.getItem("userId") != null && visButton != null)
			changeVisibilityButton();
		
		var titleBox = document.createElement("h1");
		titleBox.innerHTML = title;
		textBox.appendChild(titleBox);
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Introduzione";
		textBox.appendChild(tmpHeader);
		
		var introBox = null;
		for (i = 0; i < intro.length; i++) {
			introBox = document.createElement("div");
			introBox.setAttribute("class", "col-5 offset-3");
			introBox.innerHTML = intro[i];
			tmpHeaderBox = document.createElement("div");
			tmpHeaderBox.setAttribute("class", "col-5 offset-1")
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Sezione n° " + (i+1);
			tmpHeaderBox.append(tmpHeader)
			textBox.appendChild(tmpHeaderBox);
			textBox.appendChild(introBox);
		}
		
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Svolgimento";
		textBox.appendChild(tmpHeader);
		
		var corpusBox = null;
		for (i = 0; i < corpus.length; i++) {
			corpusBox = document.createElement("div");
			corpusBox.setAttribute("class", "col-5 offset-3");
			corpusBox.innerHTML = corpus[i];
			tmpHeaderBox = document.createElement("div");
			tmpHeaderBox.setAttribute("class", "col-5 offset-1")
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Sezione n° " + (i+1);
			tmpHeaderBox.append(tmpHeader)
			textBox.appendChild(tmpHeaderBox);
			textBox.appendChild(corpusBox);
		}
		
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Conclusione";
		textBox.appendChild(tmpHeader);
		
		var concBox = null;
		for (i = 0; i < conc.length; i++) {
			concBox = document.createElement("div");
			concBox.setAttribute("class", "col-5 offset-3");
			concBox.innerHTML = conc[i];
			tmpHeaderBox = document.createElement("div");
			tmpHeaderBox.setAttribute("class", "col-5 offset-1")
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Sezione n° " + (i+1);
			tmpHeaderBox.append(tmpHeader)
			textBox.appendChild(tmpHeaderBox);
			textBox.appendChild(concBox);
		}
		
		printComments();
		
		var authorButton = document.getElementById("authorButton");
		
		var nameBox = document.createElement("form");
		nameBox.setAttribute("style", "margin-top:2%")
		nameBox.setAttribute("action","/RnW/user");
		var inputUserId = document.createElement("input");
		inputUserId.setAttribute("type", "hidden");
		inputUserId.setAttribute("name", "userId");
		inputUserId.setAttribute("value", sessionStorage.getItem("userId"));
		nameBox.append(inputUserId);
		
		
		var inputCommId = document.createElement("input");
		inputCommId.setAttribute("type", "hidden");
		inputCommId.setAttribute("name", "textId");
		inputCommId.setAttribute("value", "${ID}");
		nameBox.append(inputCommId);
		
		var buttonName = document.createElement("input");
		buttonName.setAttribute("type","submit");
		buttonName.setAttribute("value", "Collegamento al profilo dell'autore");
		nameBox.append(buttonName);
		authorButton.append(nameBox);
	}
	
	
	
	function addComment(){
		var textId = "${ID}";
		var content = document.getElementById("commentContent").value;
		const xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			if(xhttp.readyState == 4){
				var responseText = xhttp.responseText.split("#");
				alert(responseText[0]);
				printComment(responseText[1], "${U_NAME}", content);
				comments.push([responseText[1], "$[U_NAME]", content]);
			}
		};
			xhttp.open("POST", "/RnW/comment");
		  	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded" + 
		  			"; charset=UTF-8");
			xhttp.send("textId=" + textId + "&userId=" +
					sessionStorage.getItem("userId") + 
					"&content="+ content);
		
	}
	
	function report(){
		idReported = "${ID}";
		var reportContent = prompt("Perchè vuoi segnalare questo testo?");
		const xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			console.log(xhttp.responseText);
			if(xhttp.readyState == 4){
				alert(xhttp.responseText);
			}
		};
			xhttp.open("POST", "/RnW/reportText");
		  	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded" + 
		  			"; charset=UTF-8");
			xhttp.send("idReported=" + idReported + "&report=" + reportContent + 
					"&idReporter="+ sessionData.getItem("userId"));
		  
		}
	
	function deleteComment(commentId){
		idDeleted = "${ID}";
		const xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			console.log(xhttp.responeText);
			if(xhttp.readyState == 4){
				alert(xhttp.responseText);
				comments = comments.filter(arr => !arr.includes(commentId));
				document.getElementById("commentBox").innerHTML = "";
				printComments();
			}
		};
			xhttp.open("POST", "/RnW/deleteComment");
		  	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded" + 
		  			"; charset=UTF-8");
			xhttp.send("textId=" + idDeleted + "&commentId=" + commentId);
		  
		}
	
</script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${TITLE} - RnW</title>
<style type="text/css">
p {
	white-space: pre-wrap;
}
</style>
</head>
<body>
	<div id="textBox">
	</div>

	<c:if test="${IS_AUTHOR}">
        <form method="POST" action="/RnW/writeText" style="display: inline;">
            <input type="hidden" name="userId" value="${U_ID}">
            <input type="hidden" name="textId" value="${ID}">
            <input type="submit" value="Modifica">
        </form>
     </c:if>
     <c:if test = "${IS_AUTHOR or IS_ADMIN}">   
        <form method="POST" action="/RnW/deleteText" style="display: inline;">
            <input type="hidden" name="id" value="${U_ID}">
            <input type="hidden" name="textId" value="${ID}">
            <input type="submit" value="Elimina">
        </form>
        <input type="submit" value="" class="btn btn-primary"
            onclick="changeVisibility()" id="visibilityButton">
    </c:if>
    
    
    <c:if test="${U_ID !=null}">
    <button onclick="report()" class="btn btn-secondary">Segnala Testo</button>
    </c:if>
	
    
    <div id="commentBox">
    <h1>Commenti:</h1>
    <c:if test="${U_ID != null}">
    	<input type="text" name="content" id="commentContent">
    	<button  onclick="addComment()" 
    	class="btn btn-primary">Commenta! </button>
   	
    	
       </c:if>
    </div>
    
    <form action="user" method="POST">
		<input type="text" value="${U_ID}" name="userId" hidden="true">
		<input type="text" value="${U_ID}" name="ownerId" hidden="true">
		<input type="submit" value="Vai al profilo">
	</form>

	<a href="home" class="btn btn-info" role="button">Vai alla home!</a>

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
	var tmpHeader = null
	
	function changeVisibilityButton(){
		if(visibility){
			var visButton = document.getElementById("visibilityButton");
			visButton.setAttribute("value", "Rendi Privato");
			visibility = !visibility;
		}
		else{
			var visButton = document.getElementById("visibilityButton");
			visButton.setAttribute("value", "Rendi Pubblico");
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
	
	window.onload = function(){
		changeVisibilityButton();
		
		var titleBox = document.createElement("h1");
		titleBox.innerHTML = title;
		textBox.appendChild(titleBox);
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Introduction";
		textBox.appendChild(tmpHeader);
		
		var introBox = null;
		for (i = 0; i < intro.length; i++) {
			introBox = document.createElement("p");
			introBox.innerHTML = intro[i];
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Section " + (i+1);
			textBox.appendChild(tmpHeader);
			textBox.appendChild(introBox);
		}
		
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Corpus";
		textBox.appendChild(tmpHeader);
		
		var corpusBox = null;
		for (i = 0; i < corpus.length; i++) {
			corpusBox = document.createElement("p");
			corpusBox.innerHTML = corpus[i];
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Section " + (i+1);
			textBox.appendChild(tmpHeader);
			textBox.appendChild(corpusBox);
		}
		
		tmpHeader = document.createElement("h2");
		tmpHeader.innerHTML = "Conclusion";
		textBox.appendChild(tmpHeader);
		
		var concBox = null;
		for (i = 0; i < conc.length; i++) {
			concBox = document.createElement("p");
			concBox.innerHTML = conc[i];
			tmpHeader = document.createElement("h3");
			tmpHeader.innerHTML = "Section " + (i+1);
			textBox.appendChild(tmpHeader);
			textBox.appendChild(concBox);
		}
		
		
		for (i = 0; i < comments.length; i++){
			var cBox = document.createElement("div");
			
			var nameBox = document.createElement("form");
			nameBox.setAttribute("action","/RnW/userComment");
			var inputUserId = document.createElement("input");
			inputUserId.setAttribute("hidden", "true");
			inputUserId.setAttribute("name", "userId");
			inputUserId.setAttribute("value", sessionStorage.getItem("userId"));
			nameBox.append(inputUserId);
			
			
			var inputCommId = document.createElement("input");
			inputCommId.setAttribute("hidden", "true");
			inputCommId.setAttribute("name", "commentId");
			inputCommId.setAttribute("value", comments[i][0]);
			nameBox.append(inputCommId);
			
			var inputTextId = document.createElement("input");
			inputTextId.setAttribute("hidden", "true");
			inputTextId.setAttribute("name", "textId");
			inputTextId.setAttribute("value", "${ID}");
			nameBox.append(inputTextId);
			
			var buttonName = document.createElement("input");
			buttonName.setAttribute("type","submit");
			buttonName.setAttribute("value", comments[i][1]);
			nameBox.append(buttonName);
			
			cBox.append(nameBox);
			
			var contentBox = document.createElement("p");
			contentBox.innerHTML = comments[i][2];
			cBox.append(contentBox);
			
			 <c:if test="${IS_ADMIN or IS_AUTHOR}">
			var buttonDelete = document.createElement("button");
			buttonDelete.innerHTML = "Cancella Commento";
			buttonDelete.setAttribute("onclick", "deleteComment(\"" 
					+ comments[i][0] + "\")");
			cBox.append(buttonDelete);
			</c:if>
			
			commentBox.append(cBox);
			
		}
	}
	
	function addComment(){
		var textId = "${ID}";
		var content = document.getElementById("commentContent").value;
		const xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			if(xhttp.readyState == 4){
				alert(xhttp.responseText);
				location.reload();
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
			console.log(xhttp.responeText);
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
				location.reload();
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
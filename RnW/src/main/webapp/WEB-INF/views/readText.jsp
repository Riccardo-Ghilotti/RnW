<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${TITLE} - RnW</title>
</head>
<body>
	<div id="textBox">
	</div>

	<c:if test="${IS_AUTHOR}">
        <form method="POST" action="/RnW/writeText" style="display: inline;">
            <input type="hidden" name="id" value="${U_ID}">
            <input type="hidden" name="textId" value="${ID}">
            <input type="submit" value="Modifica" class="btn btn-primary">
        </form>
    </c:if>
    
<script>
	var title = "${TITLE}";
	var intro = ${INTRO};
	var corpus = ${CORPUS};
	var conc = ${CONC};
	
	const textBox = document.getElementById("textBox");
	var tmpHeader = null
	window.onload = function(){
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
		 

	}
</script>
</body>
</html>
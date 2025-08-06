<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="False"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login - RnW</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">

</head>
<body>
	<div class="container-md text-center" style="padding-top:3cm">
	<div class="row">
	<h2>Accedi:</h2>
	</div>
	<div class="row align-items-start">
	<form action="user" Method="POST" id="loginForm">
	<div class="mb-2">
	<input type="email" name="email" class="form-control" placeholder="mail" required>
	</div>
	<div class="mb-2" id="pwd">
	<input name="password"  class="form-control" type="password" placeholder="Password" required><br>
	</div>
	<input type="submit" value="Log In!" class="btn btn-primary" id="submitButton">
	</form>
	</div>
	<div class="row align-items-start" style="padding-top:2%">
	<div class="col-6" id="registerloginButton">
	<button onclick="registerView()" id="registerButton">Register Now!</button>
	</div>
	<div class="col-6">
	<a href="home" class="btn btn-info" role="button">Vai alla home!</a>
	</div>
	</div>
	</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script>

	var formLogin = document.getElementById("loginForm");
	
	
	if("${ERROR}" != "")
		alert("${ERROR}");
		
		
	function registerView(){
		formLogin.setAttribute("action","register");
		
		var divT = document.createElement("div");
		divT.setAttribute("class", "mb-2")
		
		var t = document.createElement("input");
		t.setAttribute("type", "text");
		t.setAttribute("name", "username");
		t.setAttribute("placeholder", "username");
		t.setAttribute("id", "name");
		t.setAttribute("class", "form-control");
		t.required;
		
		
		divT.append(t);
		
		var divU = document.createElement("div");
		divU.setAttribute("class", "mb-2")
		
		var u = document.createElement("input");
		u.setAttribute("type", "password");
		u.setAttribute("name", "rPassword");
		u.setAttribute("placeholder", "Repeat Password");
		u.setAttribute("id", "rPass");
		u.setAttribute("class", "form-control");
		u.required;
		
		divU.append(u);
		
		formLogin.insertBefore(divT, document.getElementById("pwd"));
		formLogin.insertBefore(divU,document.getElementById("submitButton"));
		document.getElementById("submitButton").setAttribute("value", "Register!");
		document.getElementById("registerButton").remove();
		let f = document.createElement("button");
		f.setAttribute("onclick", "loginView()");
		f.id = "loginButton";
		f.innerHTML = "Already own an account? Log in!";
		document.getElementById("registerloginButton").append(f);
	}
	
	function loginView(){
		document.getElementById("name").remove();
		document.getElementById("rPass").remove();
		document.getElementById("submitButton").setAttribute("value", "Log In!");
		formLogin.setAttribute("action", "user");
		document.getElementById("loginButton").remove();
		let f = document.createElement("button");
		f.setAttribute("onclick","registerView()");
		f.id = "registerButton";
		f.innerHTML = "Register Now!";
		document.getElementById("registerloginButton").append(f);
	}
	
	window.onload = function(){
		sessionStorage.removeItem("userId");
	}
</script>

</body>
</html>
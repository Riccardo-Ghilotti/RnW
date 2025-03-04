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
	<form action="user" Method="POST" id="loginForm">
	<input type="text" name="username" placeholder="Username">
	<input type="text" name="password" placeholder="Password">
	<input type="submit" value="Log In!" id="submitButton">
	</form>
	<button onclick="registerView()" id="registerButton">Register Now!</button>
	

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script>

	var formLogin = document.getElementById("loginForm");
	if("${ERROR}" != "")
		alert("${ERROR}")
	function registerView(){
		formLogin.setAttribute("action","register");
		let t = document.createElement("input");
		t.setAttribute("type", "text");
		t.setAttribute("name", "rPassword");
		t.setAttribute("placeholder", "Repeat Password");
		t.setAttribute("id", "rPass")
		formLogin.insertBefore(t,document.getElementById("submitButton"));
		document.getElementById("submitButton").setAttribute("value", "Register!");
		document.getElementById("registerButton").remove();
		let f = document.createElement("button");
		f.setAttribute("onclick", "loginView()");
		f.id = "loginButton";
		f.innerHTML = "Already own an account? Log in!";
		document.body.append(f);
	}
	
	function loginView(){
		document.getElementById("rPass").remove();
		document.getElementById("submitButton").setAttribute("value", "Log In!");
		formLogin.setAttribute("action", "user");
		document.getElementById("loginButton").remove();
		let f = document.createElement("button");
		f.setAttribute("onclick","registerView()");
		f.id = "registerButton";
		f.innerHTML = "Register Now!";
		document.body.append(f);
	}
</script>

</body>
</html>
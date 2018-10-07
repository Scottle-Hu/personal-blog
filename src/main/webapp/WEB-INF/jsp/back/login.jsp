<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<script type="application/javascript" src="../js/md5.js"></script>
<script type="application/javascript" src="../js/jquery.min.js"></script>
<body onkeydown="keyOnClick(event)">
<div class="login-div">
    <h1>Huqj's Blog</h1>
    <form id="login-form" action="check" method="post">
        <input type="text" name="username" id="username" placeholder="username"/>
        <br/><br/>
        <input type="password" name="password" id="password" placeholder="password"/>
        <br/>
        <span>${errMsg }</span>
        <br/>
        <input type="button" value="login" class="login-submit"/>
    </form>
    <script type="application/javascript">
        $(document).ready(function () {
            $(".login-submit").click(function () {
                var username = $("#username").val();
                var password = $("#password").val();
                if (username == "" || password == "") {
                    alert("用户名或者密码为空");
                    return false;
                }
                password = password.MD5(32).toUpperCase();
                $("#password").val(password);
                $("#login-form").submit();
            });
        });

        function keyOnClick(e) {
            var theEvent = window.event || e;
            var code = theEvent.keyCode || theEvent.which;
            if (code == 13) {  //回车键的键值为13
                $(".login-submit").click();
                return;
            }
        }
    </script>
</div>
</body>
</html>

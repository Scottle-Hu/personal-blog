<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Console</title>
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<body>
<div class="top-menu">
    <a target="_blank" href="../index"><img src="../image/logo.jpg" class="logo"/></a>
    <span class="top-right-menu">
        <a href="account"><button type="button" class="admin-account">Admin Account</button></a>
        <a href="logout"><button type="button" class="logout">Logout</button></a>
    </span>
</div>
<div class="top-menu-blank"></div>
<div class="whole">
    <div class="left-menu">
        <ul>
            <a href="console">
                <li id="current">Overview</li>
            </a>
            <a href="blog">
                <li>Blog</li>
            </a>
            <a href="essay">
                <li>Essay</li>
            </a>
            <a href="category">
                <li>Category</li>
            </a>
            <a href="publish">
                <li>Publish</li>
            </a>
            <a href="about">
                <li>About</li>
            </a>
            <a href="message">
                <li>Message</li>
            </a>
        </ul>
    </div>
    <div class="main-board">
        HEllo I am Main board!
    </div>
</div>
</body>
</html>

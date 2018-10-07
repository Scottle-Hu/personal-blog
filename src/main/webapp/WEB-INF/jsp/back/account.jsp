<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Category List</title>
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
                <li>Overview</li>
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
        <h2>Admin Account List</h2>
        <hr/>
        <table class="essay-table">
            <tr>
                <th>id</th>
                <th>username</th>
                <th>operation</th>
            </tr>
            <c:forEach items="${adminList }" var="admin">
                <tr>
                    <td>${admin.id }</td>
                    <td>${admin.username }</td>
                    <td>
                        <button type="button" class="op-delete op-btn" opid="${admin.id }">delete</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br/>
        <button type="button" class="op-add op-btn">add</button>

        <%--悬浮层--%>
        <div class="add-admin-div">
            <form method="post" action="account/add" id="account-add-form">
                <input type="text" name="username" id="username" placeholder="username"/>
                <br/>
                <input type="password" name="password" id="password" placeholder="password"/>
                <br/>
                <input type="password" name="confirm-pass" id="confirm-pass" placeholder="confirm password"/>
                <br/><br/>
                <input type="button" value="create" id="account-add-btn"/>
                <input type="button" value="quit" id="account-add-quit-btn"/>
            </form>
        </div>

        <script type="application/javascript" src="../js/md5.js"></script>
        <script type="application/javascript" src="../js/jquery.min.js"></script>
        <script type="application/javascript">
            $(document).ready(function () {
                $(".add-admin-div").hide();
                $(".op-add").click(function () {
                    $(".add-admin-div").show();
                });
                $("#account-add-quit-btn").click(function () {
                    $("#username").val("");  //清空输入框
                    $("#password").val("");  //清空输入框
                    $("#confirm-pass").val("");  //清空输入框
                    $(".add-admin-div").hide();
                });
                $("#account-add-btn").click(function () {
                    var username = $("#username").val();
                    var password = $("#password").val();
                    var password2 = $("#confirm-pass").val();
                    if (username == "") {
                        alert("category name is blank!");
                        return false;
                    }
                    if (password == "") {
                        alert("password is blank");
                        return false;
                    }
                    if (password != password2) {
                        alert("confirm password is wrong, please check it!");
                        return false;
                    }
                    $("#password").val(password.MD5(32).toUpperCase());
                    $("#account-add-form").submit();
                });
                //删除
                $(".op-delete").click(function () {
                    if (confirm("Be sure to delete this account?")) {
                        var id = $(this).attr("opid");
                        window.location.href = "delete?type=account&id=" + id;
                    }
                });

            });
        </script>
    </div>
</div>
</body>
</html>

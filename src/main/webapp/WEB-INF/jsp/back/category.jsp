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
                <li id="current">Category</li>
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
        <h2>Category List</h2>
        <hr/>
        <table class="essay-table">
            <tr>
                <th>id</th>
                <th>category</th>
                <th>blog num</th>
                <th>operation</th>
            </tr>
            <c:forEach items="${categoryList }" var="category">
                <tr>
                    <td>${category.category.id }</td>
                    <td>${category.category.name }</td>
                    <td>${category.blogNum }</td>
                    <td>
                        <button type="button" class="op-delete op-btn" opid="${category.category.id }">delete</button>
                        <button type="button" class="op-edit op-btn" opid="${category.category.id }">edit</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br/>
        <button type="button" class="op-add op-btn">add</button>

        <%--悬浮层--%>
        <div class="add-category-div">
            <form method="post" action="category" id="category-add-form">
                <input type="text" name="name" id="category-name" placeholder="category name"/>
                <br/><br/>
                <input type="button" value="create" id="category-add-btn"/>
                <input type="button" value="quit" id="category-add-quit-btn"/>
            </form>
        </div>

        <script type="application/javascript" src="../js/jquery.min.js"></script>
        <script type="application/javascript">
            $(document).ready(function () {
                $(".add-category-div").hide();
                $(".op-add").click(function () {
                    $(".add-category-div").show();
                });
                $("#category-add-quit-btn").click(function () {
                    $("#category-name").val("");  //清空输入框
                    $(".add-category-div").hide();
                });
                $("#category-add-btn").click(function () {
                    var name = $("#category-name").val();
                    if (name == "") {
                        alert("category name is blank!");
                        return false;
                    }
                    $("#category-add-form").submit();
                });
                //删除
                $(".op-delete").click(function () {
                    if (confirm("Be sure to delete this category?")) {
                        var id = $(this).attr("opid");
                        window.location.href = "delete?type=category&id=" + id;
                    }
                });

            });
        </script>
    </div>
</div>
</body>
</html>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Publish</title>
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<!-- um编辑器 -->
<link href="../umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="../umeditor/third-party/jquery.min.js"></script>
<script type="text/javascript" src="../umeditor/third-party/template.min.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/umeditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/umeditor.min.js"></script>
<script type="text/javascript" src="../umeditor/lang/zh-cn/zh-cn.js"></script>
<%--提交文章--%>
<script type="application/javascript">
    $(document).ready(function () {
        $("#publish-btn").click(function() {
            var title = $("#title").val();
            var publishTimeStr = $("#publishTimeStr").val();
            var categoryId = $("#categoryId").val();
            var tag = $("#tag").val();
            $("#isRecommend").val($("#recommendCheckBox").prop("checked"));
            var isRecommend = $("#isRecommend").val();
            $("#htmlContent").val($("#myEditor").html());
            $("#text").val(um.getContentTxt());
            var htmlContent = $("#htmlContent").val();
            var text = $("#text").val();
            //check
            if (title == "") {
                alert("title is blank!");
                return false;
            }
            if (publishTimeStr != "" && publishTimeStr.length != 19) {
                alert("publish time format is incorrect!");
                return false;
            }
            if (categoryId == "" || categoryId == "-1") {
                alert("please select category!");
                return false;
            }
            if (isRecommend != "true" && isRecommend != "false") {
                alert("checkbox result is not boolean: " + isRecommend);
                return false;
            }
            if (text == "") {
                alert("the content is blank!");
                return false;
            }
            $("#publish-form").submit();
        });
    });
</script>
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
                <li id="current">Publish</li>
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

        <form action="publish" method="post" class="publish-form" id="publish-form">
            <input type="hidden" name="type" value="0"/>
            <label>Title&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
            <input type="text" name="title" class="publish-input" placeholder=" input the title" id="title"/>
            <br/><br/>
            <label>Publish time：</label>
            <input type="text" name="publishTimeStr" class="publish-input" placeholder=" input publish time, optional." id="publishTimeStr"/>
            <small> for example: 2018-10-02 18:55:00</small>
            <br/><br/>
            <label>Category&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
            <select class="publish-select" name="categoryId" id="categoryId">
                <option value="-1">--------please select blog category--------</option>
                <c:forEach items="${categoryList }" var="category">
                    <option value="${category.id }">${category.name }</option>
                </c:forEach>
            </select>
            <br/><br/>
            <label>Tag&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：</label>
            <input type="text" name="tag" class="publish-input tag-input" placeholder=" input tags seperated by the comma" id="tag"/>
            <small> for example: java,python,c/c++</small>
            <br/><br/>
            <label>Recommend&nbsp;:</label>
            <input type="checkbox" id="recommendCheckBox" name="recommendCheckBox" />
            <br/><br/>
            <input type="hidden" name="htmlContent" id="htmlContent"/>
            <input type="hidden" name="text" id="text"/>
            <input type="hidden" name="mdContent" id="mdContent"/>
            <input type="hidden" name="isRecommend" id="isRecommend"/>
            <script type="text/plain" id="myEditor" style="width:1000px;height:500px;"></script>
            <br/><br/>
            <input type="button" class="publish-article" value="Publish" id="publish-btn"/>
        </form>
    </div>
</div>
<script type="application/javascript">
    //实例化编辑器
    var um = UM.getEditor('myEditor');
</script>
</body>
</html>

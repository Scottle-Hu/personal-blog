<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Publish</title>
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<!-- ue编辑器 -->
<link href="../umeditor/themes/default/css/ueditor.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="../umeditor/third-party/jquery-1.10.2.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/ueditor.all.js"></script>
<script type="text/javascript" src="../umeditor/lang/zh-cn/zh-cn.js"></script>
<%--提交文章--%>
<script type="application/javascript">
    $(document).ready(function () {
        $("#publish-form-2").hide();
        $("#publish-btn").click(function() {
            var title = $("#title").val();
            var publishTimeStr = $("#publishTimeStr").val();
            var categoryId = $("#categoryId").val();
            var tag = $("#tag").val();
            $("#isRecommend").val($("#recommendCheckBox").prop("checked"));
            var isRecommend = $("#isRecommend").val();
            $("#htmlContent").val(um.getContent());
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
        /*随笔编辑处理*/
        $("#publish-btn-2").click(function() {
            var title = $("#title-2").val();
            var publishTimeStr = $("#publishTimeStr-2").val();
            $("#htmlContent-2").val(um2.getContent());
            $("#text-2").val(um2.getContentTxt());
            var htmlContent = $("#htmlContent-2").val();
            var text = $("#text-2").val();
            //check
            if (title == "") {
                alert("title is blank!");
                return false;
            }
            if (publishTimeStr != "" && publishTimeStr.length != 19) {
                alert("publish time format is incorrect!");
                return false;
            }
            if (text == "") {
                alert("the content is blank!");
                return false;
            }
            $("#publish-form-2").submit();
        });
        //博客和随笔编辑模式的切换
        $("#publish-blog-btn").click(function() {
            $("#publish-form").show();
            $("#publish-form-2").hide();
            $("#publish-blog-btn").addClass("now-tab");
            $("#publish-essay-btn").removeClass("now-tab");
        });
        $("#publish-essay-btn").click(function () {
            $("#publish-form-2").show();
            $("#publish-form").hide();
            $("#publish-essay-btn").addClass("now-tab");
            $("#publish-blog-btn").removeClass("now-tab");
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

        <div class="tab">
            <button type="button" id="publish-blog-btn" class="now-tab">Publish Blog</button>
            <button type="button" id="publish-essay-btn">Publish Essay</button>
        </div>
        <br/>

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
            <!--下面是随笔的编辑区域-->
            <form action="publish" method="post" class="publish-form" id="publish-form-2">
                <input type="hidden" name="type" value="1"/>
                <label>Title&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
                <input type="text" name="title-2" class="publish-input" placeholder=" input the title" id="title-2"/>
                    <br/><br/>
                    <label>Publish time：</label>
                <input type="text" name="publishTimeStr-2" class="publish-input" placeholder=" input publish time, optional." id="publishTimeStr-2"/>
                    <small> for example: 2018-10-02 18:55:00</small>
                <br/><br/>
                <input type="hidden" name="htmlContent-2" id="htmlContent-2"/>
                <input type="hidden" name="text-2" id="text-2"/>
                <input type="hidden" name="mdContent-2" id="mdContent-2"/>
                <script type="text/plain" id="myEditor2" style="width:1000px;height:500px;"></script>
            <br/><br/>
            <input type="button" class="publish-article" value="Publish" id="publish-btn-2"/>
        </form>
    </div>
</div>
<script type="application/javascript">
    //实例化编辑器
    var um = UE.getEditor('myEditor');  //博客
    var um2 = UE.getEditor('myEditor2');  //随笔
</script>
</body>
</html>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Publish</title>
    <link rel="stylesheet" type="text/css" href="../css/github2.css" />
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<!-- ue编辑器 -->
<link href="../umeditor/themes/default/css/ueditor.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="../umeditor/third-party/jquery-1.10.2.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="../umeditor/ueditor.all.js"></script>
<script type="text/javascript" src="../umeditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript" src="../js/marked.js"></script>
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
            //使用markdown
            if ($("#use-md").prop("checked")) {
                $("#htmlContent").val($("#html-show").html());
                $("#mdContent").val($("#md-content").val());
            } else {  //使用ue
                $("#htmlContent").val(um.getContent());
                $("#text").val(um.getContentTxt());
            }
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
            if (htmlContent == "") {
                alert("the content is blank!");
                return false;
            }
            $("#publish-form").submit();
        });
        /*随笔编辑处理*/
        $("#publish-btn-2").click(function() {
            var title = $("#title-2").val();
            var publishTimeStr = $("#publishTimeStr-2").val();
            //使用markdown
            if ($("#use-md").prop("checked")) {
                $("#htmlContent-2").val($("#html-show-2").html());
                $("#mdContent-2").val($("#md-content-2").val());
            } else {  //使用ue
                $("#htmlContent-2").val(um2.getContent());
                $("#text-2").val(um2.getContentTxt());
            }
            var htmlContent = $("#htmlContent-2").val();
            //check
            if (title == "") {
                alert("title is blank!");
                return false;
            }
            if (publishTimeStr != "" && publishTimeStr.length != 19) {
                alert("publish time format is incorrect!");
                return false;
            }
            if (htmlContent == "") {
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
        $(".md").hide();
        $("#use-md-div").click(function () {
            if ($("#use-md").prop("checked")) {
                $("#use-md").prop("checked", false);
                $(".content-type").val("1");
                $(".ue").show();
                $(".md").hide();
            } else {
                $("#use-md").prop("checked", true);
                $(".content-type").val("0");
                $(".ue").hide();
                $(".md").show();
            }
        });

        /*实时显示markdown*/
        $("#md-content").bind('input propertychange', function () {
            $("#html-show").html(marked($("#md-content").val()));
        });
        $("#md-content-2").bind('input propertychange', function () {
            $("#html-show-2").html(marked($("#md-content-2").val()));
        });
    });

    /*js绑定图片粘贴事件*/
    document.addEventListener('paste', function(event) {
        //在使用markdown编辑器的时候监听粘贴事件
        if ($("#use-md").prop("checked")) {
            var items = event.clipboardData && event.clipboardData.items;
            var file = null;
            if (items && items.length) {
                for (var i = 0; i < items.length; i++) {
                    if (items[i].type.indexOf("image") != -1) {
                        file = items[i].getAsFile();
                        /*ajax上传图片并获取图片地址粘贴到md编辑器*/
                        uploadImage(file);
                        break;
                    }
                }
            }
        }
    });

    /*ajax上传图片*/
    function uploadImage(file) {
        var formData = new FormData();
        formData.append("image", file);
        var xhr = new XMLHttpRequest();
        xhr.onload = function () {
            var result = JSON.parse(xhr.responseText);
            if (result.status == "success") {
                if ($("#publish-blog-btn").hasClass("now-tab")) {
                    $("#md-content").val($("#md-content").val() + "\n" + "![](" + result.path + ")\n");
                    $("#html-show").html(marked($("#md-content").val()));
                } else {
                    $("#md-content-2").val($("#md-content-2").val() + "\n" + "![](" + result.path + ")\n");
                    $("#html-show-2").html(marked($("#md-content-2").val()));
                }
            } else {
                alert(result.reason);
            }
        };
        xhr.open('POST', '../api/upload', true);
        xhr.send(formData);
    }

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
            <span id="use-md-div"
                  style="margin-right: 5px; margin-top: 6px; float: right;cursor: pointer;font-size: 18px;">
                <input type="checkbox" id="use-md"/>
                使用markdown语法
            </span>
        </div>
        <br/>

        <%--博客编辑区域--%>
        <form action="publish" method="post" class="publish-form" id="publish-form">
            <input type="hidden" name="type" value="0"/>
            <input type="hidden" name="contentType" class="content-type" value="1"/>
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
            <div class="ue">
                <script type="text/plain" id="myEditor" style="width:1000px;height:500px;"></script>
            </div>
            <div class="md">
                <textarea class="md-content" id="md-content"></textarea>
                <div class="html-show" id="html-show"></div>
                <div style="clear:both;"></div>
            </div>
            <br/><br/>
            <input type="button" class="publish-article" value="Publish" id="publish-btn"/>
        </form>

        <!--下面是随笔的编辑区域-->
        <form action="publish" method="post" class="publish-form" id="publish-form-2">
            <input type="hidden" name="type" value="1"/>
            <input type="hidden" name="contentType" class="content-type" value="1"/>
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
            <div class="ue">
                <script type="text/plain" id="myEditor2" style="width:1000px;height:500px;"></script>
            </div>
            <div class="md">
                <textarea class="md-content" id="md-content-2"></textarea>
                <div class="html-show" id="html-show-2"></div>
                <div style="clear:both;"></div>
            </div>
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

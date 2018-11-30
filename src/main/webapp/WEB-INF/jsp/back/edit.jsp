<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit</title>
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
        $("#publish-btn").click(function() {
            var title = $("#title").val();
            var categoryId = $("#categoryId").val();
            var tag = $("#tag").val();
            $("#isRecommend").val($("#recommendCheckBox").prop("checked"));
            var isRecommend = $("#isRecommend").val();
            //使用markdown
            if ($(".content-type").val() == "0") {
                $("#htmlContent").val($(".html-show").html());
                $("#mdContent").val($(".md-content").val());
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
            //使用markdown
            if ($(".content-type").val() == "0") {
                $("#htmlContent-2").val($(".html-show").html());
                $("#mdContent-2").val($(".md-content").val());
            } else {  //使用ue
                $("#htmlContent-2").val(um.getContent());
                $("#text-2").val(um.getContentTxt());
            }
            var htmlContent = $("#htmlContent-2").val();
            //check
            if (title == "") {
                alert("title is blank!");
                return false;
            }
            if (htmlContent == "") {
                alert("the content is blank!");
                return false;
            }
            $("#publish-form-2").submit();
        });

        /*实时显示markdown*/
        $(".md-content").bind('input propertychange', function () {
            $(".html-content").html(marked($(".md-content").val()));
            $(".html-show").html(marked($(".md-content").val()));
        });
        // 显示html样式
        $(".show-style-btn").click(function () {
            $(".show-style-btn").addClass("selected");
            $(".show-src-btn").removeClass("selected");
            $(".html-content").hide();
            $(".html-show").show();
        });
    });

    /*js绑定图片粘贴事件*/
    document.addEventListener('paste', function(event) {
        //在使用markdown编辑器的时候监听粘贴事件
        if ($(".content-type").val() == "0") {
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
                $(".md-content").val($(".md-content").val() + "\n" + "![](" + result.path + ")\n");
                $(".html-show").html(marked($(".md-content").val()));
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
                <li <c:if test="${type == 0}">id="current"</c:if>>Blog</li>
            </a>
            <a href="essay">
                <li <c:if test="${type == 1}">id="current"</c:if>>Essay</li>
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
        <br/>
        <c:if test="${type == 0}">
            <form action="edit" method="post" class="publish-form" id="publish-form">
                <input type="hidden" name="type" value="blog"/>
                <input type="hidden" name="contentType" class="content-type" value="${blog.type }"/>
                <input type="hidden" name="id" value="${id }"/>
                <label>Title&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
                <input type="text" name="title" class="publish-input" placeholder=" input the title"
                       id="title" value="${blog.title }"/>
                <br/><br/>
                <label>Category&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
                <select class="publish-select" name="categoryId" id="categoryId">
                    <option value="-1">--------please select blog category--------</option>
                    <c:forEach items="${categoryList }" var="category">
                        <option value="${category.id }" <c:if test="${blog.category.id == category.id}">selected</c:if>>${category.name }</option>
                    </c:forEach>
                </select>
                <br/><br/>
                <label>Tag&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：</label>
                <input type="text" name="tag" class="publish-input tag-input" value="${blog.tag }"
                       placeholder=" input tags seperated by the comma" id="tag"/>
                <small> for example: java,python,c/c++</small>
                <br/><br/>
                <label>Recommend&nbsp;:</label>
                <input type="checkbox" id="recommendCheckBox" name="recommendCheckBox" <c:if test="${blog.recommend }">checked</c:if> />
                <br/><br/>
                <input type="hidden" name="htmlContent" id="htmlContent"/>
                <input type="hidden" name="text" id="text"/>
                <input type="hidden" name="mdContent" id="mdContent"/>
                <input type="hidden" name="isRecommend" id="isRecommend"/>
                <c:if test="${blog.type == 1}">
                    <div class="ue">
                        <script type="text/plain" id="myEditor" style="width:1000px;height:500px;">${blog.htmlContent }</script>
                    </div>
                    <script type="application/javascript">
                        //实例化编辑器
                        var um = UE.getEditor('myEditor');
                    </script>
                </c:if>
                <c:if test="${blog.type == 0}">
                    <div class="md">
                        <textarea class="md-content">${blog.mdContent }</textarea>
                        <div class="html-show">${blog.htmlContent }</div>
                        <div style="clear:both;"></div>
                    </div>
                </c:if>
                <br/><br/>
                <input type="button" class="publish-article" value="Edit" id="publish-btn"/>
             </form>
        </c:if>

        <c:if test="${type == 1}">
            <!--下面是随笔的编辑区域-->
            <form action="edit" method="post" class="publish-form" id="publish-form-2">
                <input type="hidden" name="type" value="essay"/>
                <input type="hidden" name="contentType" class="content-type" value="${essay.type }"/>
                <input type="hidden" name="id" value="${id }"/>
                <label>Title&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</label>
                <input type="text" name="title" class="publish-input" placeholder=" input the title" id="title-2" value="${essay.title }"/>
                <br/><br/>
                <input type="hidden" name="htmlContent" id="htmlContent-2"/>
                <input type="hidden" name="text" id="text-2"/>
                <input type="hidden" name="mdContent" id="mdContent-2"/>
                <c:if test="${essay.type == 1}">
                    <div class="ue">
                        <script type="text/plain" id="myEditor" style="width:1000px;height:500px;">${essay.htmlContent }</script>
                    </div>
                    <script type="application/javascript">
                        //实例化编辑器
                        var um = UE.getEditor('myEditor');
                    </script>
                </c:if>
                <c:if test="${essay.type == 0}">
                    <div class="md">
                        <textarea class="md-content">${essay.mdContent }</textarea>
                        <div class="html-show">${essay.htmlContent }</div>
                        <div style="clear:both;"></div>
                    </div>
                </c:if>
                <br/><br/>
                <input type="button" class="publish-article" value="Edit" id="publish-btn-2"/>
            </form>
        </c:if>
    </div>
</div>
</body>
</html>

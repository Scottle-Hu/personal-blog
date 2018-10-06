<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>胡启军的个人博客</title>
    <link rel="stylesheet" href="css/calendar.css">
    <link rel="stylesheet" href="css/style.css"/>
    <script src="js/jquery.min.js"></script>
    <script src="js/calendar.js"></script>
</head>
<script>
    $(document).ready(function () {
        //点击切换博客排序方式
        $(".sort-blog li").click(function () {
            //去除原来的当前面板
            $(".sort-blog li[class=cur]").removeClass("cur");
            //添加新的当前面板
            $(this).addClass("cur");
            var requestUrl = $(this).attr("url");
            $.ajax(
                {
                    url: requestUrl,
                    type: 'json',
                    method: "GET",
                    success: function (res) {
                        //alert(res);
                        //结果是一部分html标签
                        $("#blog-list").html(res);
                    },
                    error: function () {
                        console.error("error when send ajax request to get total page num.")
                        alert("请求失败！");
                    }
                }
            );

        });
        //模拟一次点击触发ajax请求
        document.getElementById("init").click();
    });
</script>
<body>
<div class="head">
    <a href="index"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1></a>
    <ul>
        <li><a href="index">首页</a></li>
        <li><a href="blog" id="current">博客</a></li>
        <li><a href="essay">随笔</a></li>
        <li><a href="about">关于我</a></li>
        <li><a href="contact">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <div class="row-container">
        <div class="row-left">
            <h3 style="color:black;">所有博客类别</h3>
            <hr/>
            <c:forEach items="${categoryList }" var="category">
                <a href="category?id=${category.category.id }"><span
                        class="class-item">${category.category.name }(${category.blogNum })</span></a>
            </c:forEach>
        </div>
        <div id="calendar" class="calendar"></div>
    </div>
    <div class="clear"></div>
    <br/>
    <hr/>
    <ul class="sort-blog">
        <li url="api/blog/top/new" class="cur" id="init">最新博客</li>
        <li url="api/blog/top/scan">浏览最多</li>
        <li url="api/blog/top/remark">评论最多</li>
        <li url="api/blog/recommend">博主推荐</li>
    </ul>
    <div class="clear"></div>
    <br/>
    <ul class="blog-list" id="blog-list">
    </ul>
    <%--<div class="page">
        <ul>
            <li class="wider"><a href="#">首页</a></li>
            <li class="cur-page"><a href="#">1</a></li>
            <li><a href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li class="wider"><a href="#">尾页</a></li>
        </ul>
    </div>--%>
    <div class="clear"></div>
</div>
<center>
    <footer class="footer">
        <p>&copy;2018 huqj.top 版权所有</p>
        <p>友情链接：<a href="http://www.simchoir.com" target="_blank">君科沃特</a><a href="http://na.simchoir.com"
                                                                             target="_blank">seek舆情分析系统</a></p>
    </footer>
</center>
</body>
</html>
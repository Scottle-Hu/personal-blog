<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>胡启军的个人博客</title>
    <meta name="keywords" content="胡启军,个人博客,技术博客，胡启军_技术"/>
    <meta name="description" content="Hello，这里是胡启军的个人技术博客，记录一些技术学习、探索和思考，以及一些日常随笔，欢迎访问~"/>
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
<script type="application/javascript">
    /*导航栏滚动到顶部后fix*/
    $(function () {
        var elm = $('.nav-bar');
        var font = $('.head ul li a');
        var curNav = $('#current');
        var startPos = $(elm).offset().top;
        $.event.add(window, "scroll", function () {
            var p = $(window).scrollTop();
            if ((p) > startPos) {  //到顶部
                $(elm).css('position', 'fixed');
                $(elm).css('top', '0px');
                //修改背景色
                $(elm).css('background', 'black');
                $(font).css('color', 'white');
            } else { //拉回来
                $(elm).css('position', 'relative');
                //背景色改回来
                $(elm).css('background', 'white');
                $(font).css('color', 'black');
            }
            $(curNav).css('color', 'red');
        });
    });
</script>
<body>
<div class="head">
    <header class="main-header">
        <div class="header-box">
            <a href="index">
                <img src="image/logo.jpg" class="logo" alt=""/>
            </a>
        </div>
        <div class="header-branding">
            <span>HuQJ's Blog</span>
        </div>
    </header>
    <div class="nav-bar">
        <ul>
            <li><a href="index">首页</a></li>
            <li><a href="blog" id="current">博客</a></li>
            <li><a href="essay">随笔</a></li>
            <li><a href="share">分享</a></li>
            <li><a href="about">关于我</a></li>
            <li><a href="contact">联系我</a></li>
        </ul>
    </div>
</div>
<div class="main">
    <div class="main-left">
        <div class="by-category by">
            <span><img src="image/byType_icon.png"/><font class="mini-title">&nbsp;&nbsp;所有博客分类</font></span>
            <hr/>
            <div>
                <c:forEach items="${categoryList }" var="category">
                    <a href="category?id=${category.category.id }">
                        <span class="class-item">${category.category.name }(${category.blogNum })</span>
                    </a>
                </c:forEach>
            </div>
            <div class="clear"></div>
        </div>
        <%--<div class="by-date by">
            <div id="calendar" class="calendar"></div>
        </div>--%>
        <div class="clear"></div>
    </div>
    <div class="latest by">
        <ul class="sort-blog">
            <li url="api/blog/top/new" class="cur" id="init">最新博客</li>
            <li url="api/blog/top/scan">浏览最多</li>
            <li url="api/blog/top/remark">评论最多</li>
            <li url="api/blog/recommend">博主推荐</li>
        </ul>
        <div class="clear"></div>
        <hr/>
        <br/>
        <ul class="blog-list" id="blog-list">
        </ul>
    </div>
    <%--<div class="clear"></div>--%>
</div>
<div class="clear"></div>
<br/>
<center>
    <footer class="footer">
        <p>&copy;2018 huqj.top 版权所有</p>
        <p>友情链接：<a href="http://www.simchoir.com" target="_blank">君科沃特</a><a href="http://na.simchoir.com"
                                                                             target="_blank">seek舆情分析系统</a></p>
    </footer>
</center>
</body>
</html>
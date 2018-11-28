<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>胡启军的个人博客</title>
    <meta name="keywords" content="胡启军,个人博客,技术博客，胡启军_技术"/>
    <meta name="description" content="Hello，这里是胡启军的个人技术博客，记录一些技术学习、探索和思考，以及一些日常随笔，欢迎访问~"/>
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <meta http-equiv="Cache-Control" content="no-siteapp">
    <meta name="renderer" content="webkit|ie-comp|ie-stand">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no,email=no,adress=no">
    <meta name="browsermode" content="application">
    <meta name="screen-orientation" content="portrait">
    <meta name="robots" content="all"/>
    <meta name="google" content="all"/>
    <meta name="googlebot" content="all"/>
    <meta name="verify" content="all"/>

    <link rel="icon" href="image/logo.jpg" type="image/x-icon">
    <link rel="stylesheet" href="css/style.css"/>
</head>
<script type="application/javascript" src="js/jquery.min.js"></script>
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

        /*移动端菜单栏显示隐藏设置*/
        var mMenu = $('.m-menu');
        var mMenuToggle = $('.m-menu-toggle');
        var navBar = $('.m-nav-bar');
        $(mMenu).hide();
        var isHide = true;
        $(mMenuToggle).click(function () {
            if (isHide) {
                $(navBar).css("height", "130px");
                $(mMenu).show();
                isHide = false;
            } else {
                $(navBar).css("height", "40px");
                $(mMenu).hide();
                isHide = true;
            }
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
    <%--用于移动端显示的顶部菜单--%>
    <div class="m-nav-bar">
        <img src="image/menu.png" class="m-menu-toggle"/>
        <table class="m-menu">
            <tr>
                <td><a href="index">首页</a></td>
                <td><a href="blog">博客</a></td>
                <td><a href="essay">随笔</a></td>
            </tr>
            <tr>
                <td><a href="share">分享</a></td>
                <td><a href="about">关于我</a></td>
                <td><a href="contact">联系我</a></td>
            </tr>
        </table>
    </div>
</div>
<div class="clear"></div>
<div class="main">
    <div class="main-left">
        <div class="by-category by">
            <span><img src="image/byType_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客类别</font></span>
            <hr/>
            <ul>
                <c:forEach items="${categoryList }" var="category">
                    <li>
                        <a href="category?id=${category.category.id }">${category.category.name }(${category.blogNum })</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="by-date by">
            <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客日期</font></span>
            <hr/>
            <ul>
                <c:forEach items="${monthList }" var="month">
                    <li>
                        <a href="month?period=${month.monthStr }">${month.publishTime }(${month.blogNum })</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
    <div class="latest by">
        <span class="latest-span"><img src="image/list_icon.png"/><font
                class="mini-title">&nbsp;&nbsp;类别：${category.name }</font></span>
        <div class="clear"></div>
        <hr/>
        <ul>
            <c:forEach items="${blogList }" var="blog">
                <li>
                    <a href="article?id=${blog.id }">
                        <div class="blog">
                            <h3>${blog.title }</h3>
                            <p>${blog.text }</p>
                            <div class="m-blog-img">
                                <c:forEach items="${blog.imgUrls }" var="img">
                                    <img src="${img }"/>
                                </c:forEach>
                            </div>
                            <hr/>
                            <small>${blog.publishTimeStr }</small>
                            <small style="float:right;">浏览（${blog.scanNum }）</small>
                        </div>
                        <div class="blog-split"></div>
                    </a>
                </li>
            </c:forEach>
        </ul>
        <div class="page">
            <ul id="pageUl">
            </ul>
        </div>
        <script type="application/javascript" src="js/jquery.min.js"></script>
        <script type="application/javascript">
            $(document).ready(function () {
                var url = window.location.href;
                var pageIndex = url.indexOf("page=");
                var curPage = 1;
                if (pageIndex != -1) {
                    var endIndex;
                    var t = url.indexOf("&", pageIndex);
                    if (t != -1) {
                        endIndex = t;
                    } else {
                        endIndex = url.length;
                    }
                    curPage = url.substring(pageIndex + 5, Math.min(url.length, endIndex));
                }
                var idIndex = url.indexOf("id=");
                var id = -1;
                if (idIndex == -1) {
                    alert("类别id为空！");
                } else {
                    var endIndex;
                    var t = url.indexOf("&", idIndex);
                    if (t != -1) {
                        endIndex = t;
                    } else {
                        endIndex = url.length;
                    }
                    id = url.substring(idIndex + 3, Math.min(url.length, endIndex));
                }
                if (id == -1) {
                    alert("获取类别id出错！");
                }
                //ajax取得该类别总页面数目
                var totalPage = 1;
                $.ajax(
                    {
                        url: "api/blog/page",
                        type: 'json',
                        method: "GET",
                        dataType: "json",
                        data: {
                            "type": "category",
                            "categoryId": id
                        },
                        success: function (res) {
                            var result = eval(res);
                            totalPage = result.totalPage;
                            if (totalPage < curPage || curPage < 1) {  //当有人篡改page参数时
                                curPage = 1;
                            }
                            paintPageNavigator(curPage, totalPage, id);
                        },
                        error: function () {
                            console.error("error when send ajax request to get total page num.")
                            paintPageNavigator(curPage, 0, id);
                        }
                    }
                );

            });

            //画分页
            function paintPageNavigator(curPage, totalPage, id) {
                var delta = 1;  //当前页前后显示的页数，可调节
                var liStr = '<li class="wider"><a href="category?id=' + id + '">首页</a></li> ';
                if (curPage - delta > 2) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                for (var i = curPage - delta; i <= parseInt(curPage) + parseInt(delta); i++) {
                    if (i == curPage) {
                        liStr += '<li class="cur-page"><a href="#">' + curPage + '</a></li>';
                    } else if (i >= 1 && i <= totalPage) {
                        liStr += '<li><a href="category?id=' + id + '&page=' + i + '">' + i + '</a></li>';
                    }
                }
                if (parseInt(curPage) + delta + 1 < totalPage) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                liStr += '<li class="wider"><a href="category?id=' + id + '&page=' + totalPage + '">尾页</a></li>';
                $("#pageUl").html(liStr);
            }

        </script>
    </div>
    <div class="clear"></div>
</div>
<%--切换成移动端的时候在底部显示--%>
<div class="m-bottom">
    <div class="bottom">
        <span><img src="image/search_icon.png"/><font class="mini-title">&nbsp;&nbsp;站内搜索</font></span>
        <hr/>
        <div class="search-box">
            <input type="text" id="search-text-bottom" placeholder=" 输入关键词搜索"/>
            <input type="button" value="搜索"/>
        </div>
    </div>
    <div class="bottom">
        <span><img src="image/byType_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客类别</font></span>
        <hr/>
        <ul>
            <c:forEach items="${categoryList }" var="category">
                <li>
                    <a href="category?id=${category.category.id }">${category.category.name }(${category.blogNum })</a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="bottom">
        <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客日期</font></span>
        <hr/>
        <ul>
            <c:forEach items="${monthList }" var="month">
                <li>
                    <a href="month?period=${month.monthStr }">${month.publishTime }(${month.blogNum })</a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
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
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>胡启军的个人博客</title>
    <meta name="keywords" content="胡启军,个人博客,技术博客，胡启军_技术"/>
    <meta name="description" content="Hello，这里是胡启军的个人技术博客，记录一些技术学习、探索和思考，以及一些日常随笔，欢迎访问~"/>
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body>
<div class="head">
    <a href="index"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1>
    </a>
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
        <span class="latest-span"><img src="image/list_icon.png"/><font class="mini-title">&nbsp;&nbsp;时间：${month }</font></span>
        <div class="clear"></div>
        <hr/>
        <ul>
            <c:forEach items="${blogList }" var="blog">
                <li>
                    <a href="article?id=${blog.id }">
                        <div class="blog">
                            <h4>${blog.title }</h4>
                            <p>${blog.text }</p>
                            <c:forEach items="${blog.imgUrls }" var="img">
                                <img src="${img }"/>
                            </c:forEach>
                            <hr/>
                            <small>${blog.publishTimeStr }</small>
                            <small style="float:right;">浏览（${blog.scanNum }）</small>
                        </div>
                    </a>
                </li>
            </c:forEach>
        </ul>
        <hr/>
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
                var monthIndex = url.indexOf("period=");
                var month = "";
                if (monthIndex == -1) {
                    alert("时间为空！");
                } else {
                    var endIndex;
                    var t = url.indexOf("&", monthIndex);
                    if (t != -1) {
                        endIndex = t;
                    } else {
                        endIndex = url.length;
                    }
                    month = url.substring(monthIndex + 7, Math.min(url.length, endIndex));
                }
                if (month == "") {
                    alert("获取月份出错！");
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
                            "type": "month",
                            "month": month
                        },
                        success: function (res) {
                            var result = eval(res);
                            totalPage = result.totalPage;
                            if (totalPage < curPage || curPage < 1) {  //当有人篡改page参数时
                                curPage = 1;
                            }
                            paintPageNavigator(curPage, totalPage, month);
                        },
                        error: function () {
                            console.error("error when send ajax request to get total page num.")
                            paintPageNavigator(curPage, 0, month);
                        }
                    }
                );

            });

            //画分页
            function paintPageNavigator(curPage, totalPage, month) {
                var delta = 1;  //当前页前后显示的页数，可调节
                var liStr = '<li class="wider"><a href="month?period=' + month + '">首页</a></li> ';
                if (curPage - delta > 2) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                for (var i = curPage - delta; i <= parseInt(curPage) + parseInt(delta); i++) {
                    if (i == curPage) {
                        liStr += '<li class="cur-page"><a href="#">' + curPage + '</a></li>';
                    } else if (i >= 1 && i <= totalPage) {
                        liStr += '<li><a href="month?period=' + month + '&page=' + i + '">' + i + '</a></li>';
                    }
                }
                if (parseInt(curPage) + delta + 1 < totalPage) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                liStr += '<li class="wider"><a href="month?period=' + month + '&page=' + totalPage + '">尾页</a></li>';
                $("#pageUl").html(liStr);
            }

        </script>
    </div>
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
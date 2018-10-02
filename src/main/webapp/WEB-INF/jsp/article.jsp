<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>胡启军的个人博客</title>
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body>
<div class="head">
    <a href="index"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1></a>
    <ul>
        <li><a href="index">首页</a></li>
        <li><a href="blog" <c:if test="${type == 0}">id="current"</c:if>>博客</a></li>
        <li><a href="essay" <c:if test="${type == 1}">id="current"</c:if>>随笔</a></li>
        <li><a href="about">关于我</a></li>
        <li><a href="contact">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <div class="main-left">
        <c:if test="${type == 0 }">
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
        </c:if>
        <div class="by-date by">
            <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;按<c:if
                    test="${type == 0 }">博客</c:if><c:if test="${type == 1 }">随笔</c:if>日期</font></span>
            <hr/>
            <ul>
                <c:forEach items="${monthList }" var="month">
                    <li>
                        <a href="month?period=${month.publishTime }">${month.publishTime }(${month.blogNum })</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
    <div class="latest by">
        <span><img src="image/blog_show_icon.png"/><font class="mini-title">&nbsp;&nbsp;<c:if
                test="${type == 0 }">博客</c:if><c:if test="${type == 1 }">随笔</c:if>内容</font></span>
        <div class="clear"></div>
        <hr/>
        <c:if test="${type == 0 }">
            <div>
                <center><h3>${blog.title }</h3></center>
                <center>
                    <small style="color:gray;">
                        发布时间：[${blog.publishTimeStr }]&nbsp;&nbsp;&nbsp;&nbsp;
                        博客分类：[${blog.category.name }]&nbsp;&nbsp;&nbsp;&nbsp;
                        浏览数量：（${blog.scanNum }）
                    </small>
                </center>
                <p class="article">${blog.htmlContent }</p>
                <small><b>标签：</b>${blog.tag }</small>
            </div>
        </c:if>
        <c:if test="${type == 1 }">
            <%--TODO--%>
        </c:if>
        <hr/>
        <c:if test="${type == 0 }">
            <div class="neiborhood">
                <br/>
                <b>上一篇:</b><c:if test="${previousBlog != null }">
                <a href="article?id=${previousBlog.id }">${previousBlog.title }</a></c:if>
                <c:if test="${previousBlog == null }"><a href="#" disabled="true">没有了</a></c:if>
                <br/>
                <br/>
                <b>下一篇:</b><c:if test="${nextBlog != null }">
                <a href="article?id=${nextBlog.id }">${nextBlog.title }</a></c:if>
                <c:if test="${nextBlog == null }"><a href="#" disabled="true">没有了</a></c:if>
                <br/>
                <br/>
            </div>
        </c:if>
        <c:if test="${type == 1 }">
            <%--TODO--%>
        </c:if>
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
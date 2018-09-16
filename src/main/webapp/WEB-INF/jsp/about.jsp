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
    <a href="index.jspindex.html"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1>
    </a>
    <ul>
        <li><a href="index.jsp">首页</a></li>
        <li><a href="blog.jsp">博客</a></li>
        <li><a href="essay.jsp">随笔</a></li>
        <li><a href="about.jsp" id="current">关于我</a></li>
        <li><a href="contact.jsp">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <div class="row-container">
        <div class="row-left" style="width:300px;">
            <img src="image/logo.jpg" width=250px style="border-radius:10px;"/>
        </div>
        <div class="person-info">
            <ul>
                <li>
                    <b>姓名</b>：胡启军
                </li>
                <li>
                    <b>英文名</b>：Charles
                </li>
                <li>
                    <b>学校</b>：武汉大学（本科）
                </li>
                <li>
                    <b>专业</b>：信息管理与信息系统
                </li>
                <li>
                    <b>兴趣爱好</b>：吉他、音乐、电影
                </li>
                <li>
                    <b>座右铭</b>：做想做的事，成为想成为的人。
                </li>
                <li>
                    <b>github</b>：<a href="https://github.com/Scottle-Hu/" target="_blank">https://github.com/Scottle-Hu/</a>
                </li>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
    <hr/>
    <div class="row-container experience">
        <h3>教育经历</h3>
        <ul>
            <li>2015.9~2019.6 武汉大学 信息管理与信息系统</li>
        </ul>
    </div>
    <hr/>
    <div class="row-container experience">
        <h3>工作经历</h3>
        <ul>
            <li>2018.4~2018.10 网易有道实习 计算广告组后台研发</li>
            <li>2017.12~2018.3 科大讯飞武汉研发中心实习 从事后台开发</li>
        </ul>
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
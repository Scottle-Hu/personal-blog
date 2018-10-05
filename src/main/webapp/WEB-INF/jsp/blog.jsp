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
            //TODO 相应的切换博客列表内容

        });
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
            <a href="#"><span class="class-item">java基础(10)</span></a>
            <span class="class-item">java web(15)</span>
            <span class="class-item">Linux(10)</span>
            <span class="class-item">docker(10)</span>
            <span class="class-item">hadoop(10)</span>
            <span class="class-item">舆情分析系统(4)</span>
            <span class="class-item">java基础(10)</span>
            <span class="class-item">java基础(10)</span>
            <span class="class-item">java基础(10)</span>
        </div>
        <div id="calendar" class="calendar"></div>
    </div>
    <div class="clear"></div>
    <br/>
    <hr/>
    <ul class="sort-blog">
        <li class="cur">最新博客</li>
        <li>浏览最多</li>
        <li>评论最多</li>
        <li>博主推荐</li>
    </ul>
    <div class="clear"></div>
    <br/>
    <ul class="blog-list">
        <li>
            <a href="#">
                <div class="blog">
                    <h4>使用vmware搭建centos虚拟机集群记录</h4>
                    <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                    <img src="image/logo.jpg"/>
                    <img src="image/list_icon.png"/>
                    <br/><br/>
                    <small>2018-03-21 10:34</small>
                    <small style="float:right;">浏览（123）</small>
                    <hr/>
                </div>
            </a>
        </li>
        <li>
            <a href="#">
                <div class="blog">
                    <h4>使用vmware搭建centos虚拟机集群记录</h4>
                    <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                    <img src="image/logo.jpg"/>
                    <img src="image/list_icon.png"/>
                    <br/><br/>
                    <small>2018-03-21 10:34</small>
                    <small style="float:right;">浏览（123）</small>
                    <hr/>
                </div>
            </a>
        </li>
        <li>
            <a href="#">
                <div class="blog">
                    <h4>使用vmware搭建centos虚拟机集群记录</h4>
                    <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                    <img src="image/logo.jpg"/>
                    <img src="image/list_icon.png"/>
                    <br/><br/>
                    <small>2018-03-21 10:34</small>
                    <small style="float:right;">浏览（123）</small>
                    <hr/>
                </div>
            </a>
        </li>
    </ul>
    <div class="page">
        <ul>
            <li class="wider"><a href="#">首页</a></li>
            <li class="cur-page"><a href="#">1</a></li>
            <li><a href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li class="wider"><a href="#">尾页</a></li>
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
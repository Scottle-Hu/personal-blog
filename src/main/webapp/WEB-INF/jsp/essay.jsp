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
        <li><a href="blog">博客</a></li>
        <li><a href="essay" id="current">随笔</a></li>
        <li><a href="about">关于我</a></li>
        <li><a href="contact">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <div class="main-left">
        <div class="by-type by" style="margin-top:10px;">
            <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;随笔日期</font></span>
            <hr/>
            <ul>
                <li><a href="#">2017年12月(20)</a></li>
                <li><a href="#">2018年1月(10)</a></li>
                <li><a href="#">2018年4月(10)</a></li>
                <li><a href="#">2018年5月(6)</a></li>
                <li><a href="#">2018年6月(30)</a></li>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
    <div class="latest by">
        <span><img src="image/list_icon.png"/><font class="mini-title">&nbsp;&nbsp;最新随笔</font></span>
        <div class="clear"></div>
        <hr/>
        <ul>
            <li>
                <a href="#">
                    <div class="blog">
                        <h4>使用vmware搭建centos虚拟机集群记录</h4>
                        <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                        <hr/>
                        <small>2018-03-21 10:34</small>
                        <small style="float:right;">浏览（123）</small>
                    </div>
                </a>
            </li>
            <li>
                <a href="#">
                    <div class="blog">
                        <h4>使用vmware搭建centos虚拟机集群记录</h4>
                        <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                        <hr/>
                        <small>2018-03-21 10:34</small>
                        <small style="float:right;">浏览（123）</small>
                    </div>
                </a>
            </li>
            <li>
                <a href="#">
                    <div class="blog">
                        <h4>使用vmware搭建centos虚拟机集群记录</h4>
                        <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析，但是贫穷如我，买不起那么多云服务器，只能尝试在本地用虚拟机搭建一个虚拟集群......</p>
                        <hr/>
                        <small>2018-03-21 10:34</small>
                        <small style="float:right;">浏览（123）</small>
                    </div>
                </a>
            </li>
        </ul>
        <hr/>
        <div class="page">
            <ul>
                <li class="wider"><a href="#">首页</a></li>
                <li class="cur-page"><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li class="wider"><a href="#">尾页</a></li>
            </ul>
        </div>
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
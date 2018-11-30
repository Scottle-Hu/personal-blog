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

        //test,别让页脚跑到上面来了
        var foot = $('.footer');
        var footTop = $(foot).offset().top;
        if (footTop + $(foot).height() < $(window).height()) {
            $(foot).css('position', 'fixed');
            $(foot).css('width', '100%');
            $(foot).css('bottom', '0');
        }
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
            <li><a href="blog">博客</a></li>
            <li><a href="essay">随笔</a></li>
            <li><a href="share" id="current">分享</a></li>
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
<div class="single-row">
    <div class="row-container experience">
        <h3>资源分享页面开发中~敬请期待</h3>
        <ul>
        </ul>
    </div>
    <hr/>
    <div class="clear"></div>
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
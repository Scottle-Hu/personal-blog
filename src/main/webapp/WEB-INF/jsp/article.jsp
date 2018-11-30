<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title><c:if test="${type == 0 }">${blog.title }</c:if><c:if
            test="${type == 1 }">${essay.title }</c:if>_胡启军个人博客</title>
    <meta charset="UTF-8"/>
    <meta name="keywords" content="胡启军,个人博客,技术博客, ${blog.title }"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
    <link rel="stylesheet" type="text/css" href="css/github.css"/>
    <link rel="stylesheet" href="css/style.css"/>
    <%--代码显示样式--%>
    <link href="umeditor/third-party/SyntaxHighlighter/shCoreDefault.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="umeditor/third-party/SyntaxHighlighter/shCore.js"></script>
    <script type="application/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript">
        SyntaxHighlighter.all();
        var id;
        $(document).ready(function () {
            var url = window.location.href;
            id = url.substring(url.indexOf("id=") + 3, url.length);
            getRemarks(id);
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
</head>
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
            <li><a href="blog" <c:if test="${type == 0}">id="current"</c:if>>博客</a></li>
            <li><a href="essay" <c:if test="${type == 1}">id="current"</c:if>>随笔</a></li>
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
        <c:if test="${type == 0 }">
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
        </c:if>
        <c:if test="${type == 1 }">
            <div class="by-category by">
                <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;随笔日期</font></span>
                <hr/>
                <ul>
                    <c:forEach items="${monthList }" var="month">
                        <li>
                            <a href="monthessay?period=${month.primitiveMonthStr }">${month.month }(${month.num })</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div class="clear"></div>
    </div>
    <div class="latest by article-div">
        <span class="latest-span"><img src="image/blog_show_icon.png"/><font class="mini-title">&nbsp;&nbsp;<c:if
                test="${type == 0 }">博客</c:if><c:if test="${type == 1 }">随笔</c:if>内容</font></span>
        <div class="clear"></div>
        <hr/>
        <c:if test="${type == 0 }">
            <div class="article-wrap">
                <center><h2>${blog.title }</h2></center>
                <center>
                    <small style="color:gray;">
                        发布时间：[${blog.publishTimeStr }]&nbsp;&nbsp;&nbsp;&nbsp;
                        博客分类：[${blog.category.name }]&nbsp;&nbsp;&nbsp;&nbsp;
                        浏览数量：（${blog.scanNum }）
                    </small>
                </center>
                <div class="article">${blog.htmlContent }</div>
                <small><b>标签：</b>${blog.tag }</small>
            </div>
        </c:if>
        <c:if test="${type == 1 }">
            <div class="article-wrap">
                <center><h2>${essay.title }</h2></center>
                <center>
                    <small style="color:gray;">
                        发布时间：[${essay.publishTimeStr }]&nbsp;&nbsp;&nbsp;&nbsp;
                        浏览数量：（${essay.scanNum }）
                    </small>
                </center>
                <div class="article">${essay.htmlContent }</div>
            </div>
        </c:if>
        <hr/>
        <c:if test="${type == 0 }">
            <div class="neiborhood">
                <br/>
                <b>上一篇:</b><c:if test="${previousBlog != null }">
                <a href="article?id=${previousBlog.id }">${previousBlog.title }</a></c:if>
                <c:if test="${previousBlog == null }"><a disabled="true">没有了</a></c:if>
                <br/>
                <br/>
                <b>下一篇:</b><c:if test="${nextBlog != null }">
                <a href="article?id=${nextBlog.id }">${nextBlog.title }</a></c:if>
                <c:if test="${nextBlog == null }"><a disabled="true">没有了</a></c:if>
                <br/>
                <br/>
            </div>
        </c:if>
        <c:if test="${type == 1 }">
            <div class="neiborhood">
                <br/>
                <b>上一篇:</b><c:if test="${previousEssay != null }">
                <a href="article?type=essay&id=${previousEssay.id }">${previousEssay.title }</a></c:if>
                <c:if test="${previousEssay == null }"><a disabled="true">没有了</a></c:if>
                <br/>
                <br/>
                <b>下一篇:</b><c:if test="${nextEssay != null }">
                <a href="article?type=essay&id=${nextEssay.id }">${nextEssay.title }</a></c:if>
                <c:if test="${nextEssay == null }"><a disabled="true">没有了</a></c:if>
                <br/>
                <br/>
            </div>
        </c:if>
        <div class="blog-split"></div>
        <%--评论区--%>
        <div class="remark-div">
            <span class="latest-span"><img src="image/remark.png"/>
                <span class="mini-title">&nbsp;&nbsp;所有评论</span>
            </span>
            <div class="clear"></div>
            <hr/>
            <%--未登录--%>
            <c:if test="${userInfo == null}">
                您尚未登录！选择登录方式登录后方可评论~&nbsp;&nbsp;&nbsp;&nbsp;
                <img src="image/github_icon.jpg" alt="github" title="github" class="oauth-login-logo"
                     onclick="javascript:window.location.href = 'https://github.com/login/oauth/authorize?client_id=7cc6cce09d315f877b82&redirect_uri=http%3a%2f%2fwww.huqj.top%2foauth%2fgithub&state='+UrlEncode(window.location.href);"/>
                <img src="image/qq_icon.png" alt="qq" title="qq" class="oauth-login-logo" style="border-radius: 0;"
                     onclick="javascript:window.location.href = 'https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101529384&redirect_uri=http%3a%2f%2fwww.huqj.top%2foauth%2fqq&state='+UrlEncode(window.location.href);"/>
            </c:if>
            <%--已经登录，可以发表评论--%>
            <c:if test="${userInfo != null}">
                - <img src="${userInfo.iconUrl }" class="user-icon"
                       onclick="javascript:window.location.href='${userInfo._3rdPartyHomeUrl }';"/>
                <span class="username">&nbsp;&nbsp;${userInfo.username }</span>
                <span style="color: gray;font-size: small;">&nbsp;&nbsp;&nbsp;&nbsp;【您已登录，欢迎评论~】
                <a style="cursor: pointer;text-decoration: underline;color: blue;"
                   onclick="javascript:window.location.href='/oauth/logout?originUrl='+UrlEncode(window.location.href);">退出登录</a>
            </span>
                <br/><br/>
                <textarea id="remark-text"></textarea>
                <br/>
                <button type="button" class="remark-btn" onclick="publishRemark()">发表评论</button>
            </c:if>
            <hr/>
            <%--评论数据--%>
            <ul class="remark-list"></ul>
            <script type="application/javascript">
                //获取评论html
                function getRemarks(id) {
                    $.ajax(
                        {
                            url: "/api/remarks?articleId=" + id,
                            type: 'json',
                            method: "GET",
                            success: function (res) {
                                //结果是一部分html标签
                                $(".remark-list").html(res);
                            },
                            error: function () {
                                console.error("error when send ajax request to get remarks.")
                                alert("评论信息请求失败！");
                            }
                        }
                    );
                }

                //发表评论
                function publishRemark() {
                    var content = $("#remark-text").val();
                    if (content == null || content == "") {
                        alert("评论内容不能为空哦~");
                        return false;
                    }
                    $.ajax(
                        {
                            url: "/remark",
                            type: 'json',
                            method: "POST",
                            dataType: "json",
                            data: {
                                content: content,
                                articleId: id
                            },
                            success: function (res) {
                                $("#remark-text").val("");
                                alert("评论成功！");
                                getRemarks(id);  //刷新评论内容
                            },
                            error: function () {
                                console.error("error when send ajax to remark.");
                                $("#remark-text").val("");
                                getRemarks(id);  //刷新评论内容
                            }
                        }
                    );
                }
            </script>
            <br/>
        </div>
    </div>
</div>
<div class="clear"></div>
</div>
<%--切换成移动端的时候在底部显示--%>
<div class="m-bottom">
    <c:if test="${type == 0}">
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
    </c:if>
    <c:if test="${type == 1}">
        <div class="bottom">
            <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;随笔日期</font></span>
            <hr/>
            <ul>
                <c:forEach items="${monthList }" var="month">
                    <li>
                        <a href="monthessay?period=${month.primitiveMonthStr }">${month.month }(${month.num })</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
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

<script>
    function UrlEncode(str) {
        return transform(str);
    }

    function transform(s) {
        var hex = '';
        var i, j, t;

        j = 0;
        for (i = 0; i < s.length; i++) {
            t = hexfromdec(s.charCodeAt(i));
            if (t == '25') {
                t = '';
            }
            hex += '%' + t;
        }
        return hex;
    }

    function hexfromdec(num) {
        if (num > 65535) {
            return ("err!")
        }
        first = Math.round(num / 4096 - .5);
        temp1 = num - first * 4096;
        second = Math.round(temp1 / 256 - .5);
        temp2 = temp1 - second * 256;
        third = Math.round(temp2 / 16 - .5);
        fourth = temp2 - third * 16;
        return ("" + getletter(third) + getletter(fourth));
    }

    function getletter(num) {
        if (num < 10) {
            return num;
        }
        else {
            if (num == 10) {
                return "A"
            }
            if (num == 11) {
                return "B"
            }
            if (num == 12) {
                return "C"
            }
            if (num == 13) {
                return "D"
            }
            if (num == 14) {
                return "E"
            }
            if (num == 15) {
                return "F"
            }
        }
    }
</script>
</html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title><c:if test="${type == 0 }">${blog.title }</c:if><c:if
            test="${type == 1 }">${essay.title }</c:if>_胡启军个人博客</title>
    <meta name="keywords" content="胡启军,个人博客,技术博客, ${blog.title }"/>
    <link rel="stylesheet" href="css/style.css"/>
    <%--代码显示样式--%>
    <link href="umeditor/third-party/SyntaxHighlighter/shCoreDefault.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="umeditor/third-party/SyntaxHighlighter/shCore.js"></script>
    <%--接入QQ登录--%>
    <script type="text/javascript" src="http://qzonestyle.gtimg.cn/qzone/openapi/qc_loader.js"
            data-appid="1107904871" charset="utf-8"></script>
    <script type="text/javascript">
        SyntaxHighlighter.all();
    </script>
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
            <div class="by-type by" style="margin-top:10px;">
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
    <div class="latest by">
        <span class="latest-span"><img src="image/blog_show_icon.png"/><font class="mini-title">&nbsp;&nbsp;<c:if
                test="${type == 0 }">博客</c:if><c:if test="${type == 1 }">随笔</c:if>内容</font></span>
        <div class="clear"></div>
        <hr/>
        <c:if test="${type == 0 }">
            <div class="article-wrap">
                <center><h3>${blog.title }</h3></center>
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
                <center><h3>${essay.title }</h3></center>
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
            <div class="neiborhood">
                <br/>
                <b>上一篇:</b><c:if test="${previousEssay != null }">
                <a href="article?type=essay&id=${previousEssay.id }">${previousEssay.title }</a></c:if>
                <c:if test="${previousEssay == null }"><a href="#" disabled="true">没有了</a></c:if>
                <br/>
                <br/>
                <b>下一篇:</b><c:if test="${nextEssay != null }">
                <a href="article?type=essay&id=${nextEssay.id }">${nextEssay.title }</a></c:if>
                <c:if test="${nextEssay == null }"><a href="#" disabled="true">没有了</a></c:if>
                <br/>
                <br/>
            </div>
        </c:if>
    </div>
    <div class="clear"></div>
    <span id="qqLoginBtn"></span>
    <script type="text/javascript">
        QC.Login({
            btnId: "qqLoginBtn"	//插入按钮的节点id
        });

        function getUserInfo() {
            var paras = {};
            if (QC.Login.check()) {//如果已登录
                QC.Login.getMe(function (openId, accessToken) {
                    console.log(["当前登录用户的", "openId为：" + openId, "accessToken为：" + accessToken].join("\n"));
                    paras["openId"] = openId;
                    paras["accessToken"] = accessToken;
                });
                //这里可以调用自己的保存接口
                //...
            }
            QC.api("get_user_info", paras)
                .success(function (s) {//成功回调
                    alert("获取用户信息成功！当前用户昵称为：" + s.data.nickname);
                })
                .error(function (f) {//失败回调
                    alert("获取用户信息失败！");
                })
                .complete(function (c) {//完成请求回调
                    alert("获取用户信息完成！");
                });
        }
    </script>
    <a href="javascript:QC.Login.signOut();" style="margin-left:6px;">退出</a>
    <a href="javascript:getUserInfo();" style="margin-left:6px;">getInfo</a>
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
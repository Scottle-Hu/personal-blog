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
    <a href="index"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1></a>
    <ul>
        <li><a href="index">首页</a></li>
        <li><a href="blog">博客</a></li>
        <li><a href="essay">随笔</a></li>
        <li><a href="about">关于我</a></li>
        <li><a href="contact" id="current">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <h3>联系方式</h3>
    <ul class="contact-info">
        <li>邮箱：2477889605@qq.com</li>
        <li>TEL：13277988636</li>
        <li>QQ：2477889605</li>
    </ul>
    <hr/>
    <div class="twoDCode">
        <h3>微信</h3>
        <img src="image/wechat.png" width=250px>
    </div>
    <div class="message">
        <h3>发送消息</h3>
        <div class="rowElem">
            <input type="text" value="Name:" name="name" id="name"
                   onBlur="if(this.value=='') this.value='Name:'"
                   onFocus="if(this.value =='Name:' ) this.value=''"/>
        </div>
        <div class="rowElem">
            <input type="Email" value="E-mail:" name="email" id="email"
                   onBlur="if(this.value=='') this.value='E-mail:'"
                   onFocus="if(this.value =='E-mail:' ) this.value=''"/>
        </div>
        <div class="rowElem">
            <input type="text" value="Phone:" name="phone" id="phone"
                   onBlur="if(this.value=='') this.value='Phone:'"
                   onFocus="if(this.value =='Phone:' ) this.value=''"/>
        </div>
        <div class="rowElem2">
						<textarea rows="20" cols="70" name="message" id="message"
                                  onBlur="if(this.value=='') this.value='Message:'"
                                  onFocus="if(this.value =='Message:' ) this.value=''">Message:</textarea>
        </div>
        <button type="button">提交</button>
        <button type="button">清空</button>
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
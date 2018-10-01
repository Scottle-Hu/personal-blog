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
    <a href="index.html"><h1><img src="image/logo.jpg" class="logo" alt=""/>&nbsp;&nbsp;&nbsp;HuQJ's Blog</h1></a>
    <ul>
        <li><a href="index.html">首页</a></li>
        <li><a href="blog.html" id="current">博客</a></li>
        <li><a href="essay.html">随笔</a></li>
        <li><a href="about.html">关于我</a></li>
        <li><a href="contact.html">联系我</a></li>
    </ul>
</div>
<div class="clear"></div>
<div class="main">
    <div class="main-left">
        <div class="by-category by">
            <span><img src="image/byType_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客类别</font></span>
            <hr/>
            <ul>
                <li><a href="#">java基础(20)</a></li>
                <li><a href="#">javaweb(10)</a></li>
                <li><a href="#">linux(10)</a></li>
                <li><a href="#">spark(6)</a></li>
                <li><a href="#">前端(30)</a></li>
                <li><a href="#">java基础(20)</a></li>
                <li><a href="#">javaweb(10)</a></li>
                <li><a href="#">linux(10)</a></li>
                <li><a href="#">spark(6)</a></li>
                <li><a href="#">前端(30)</a></li>
                <li><a href="#">java基础(20)</a></li>
                <li><a href="#">javaweb(10)</a></li>
                <li><a href="#">linux(10)</a></li>
                <li><a href="#">spark(6)</a></li>
                <li><a href="#">前端(30)</a></li>
                <li><a href="#">java基础(20)</a></li>
                <li><a href="#">javaweb(10)</a></li>
                <li><a href="#">linux(10)</a></li>
                <li><a href="#">spark(6)</a></li>
                <li><a href="#">前端(30)</a></li>
            </ul>
        </div>
        <div class="by-date by">
            <span><img src="image/byDate_icon.png"/><font class="mini-title">&nbsp;&nbsp;按博客日期</font></span>
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
        <span><img src="image/blog_show_icon.png"/><font class="mini-title">&nbsp;&nbsp;博客内容</font></span>
        <div class="clear"></div>
        <hr/>
        <div>
            <center><h3>理解了上面两个配置就基本可以无需考虑的写后端接口</h3></center>
            <center>
                <small style="color:gray;">
                    发布时间：[2018-08-21 10:31]&nbsp;&nbsp;&nbsp;&nbsp;
                    博客分类：[shiro]&nbsp;&nbsp;&nbsp;&nbsp;
                    浏览数量：（12）
                </small>
            </center>
            <p class="article">
                <code>
                    学到这里，我才终于感受到了shiro的强大之处。
                    想一想，我们之前做web应用，例如最基本的servlet应用的时候是怎么进行用户鉴权和授权的呢？当用户访问某个需要验证的页面时，我们会判断request或者session中有没有我们设置的登录标识，如果没有则进行登录的逻辑：去数据库查找，匹配，如果验证失败就返回错误信息并转发到登录页面，如果验证成功则在session中设置登录标识，并跳往首页。
                    当涉及到权限角色的问题时，就更加麻烦了，每次需要访问有权限要求的servlet的时候，都需要根据session中当前用户的信息走一遍权限验证的逻辑。

                    而使用shiro就比较简单了，shiro与web结合的原理就是shiro可以通过web.xml的配置对于指定的请求做filter，包括判断当前请求对应的主体(subject)是否验证通过，是否具有相应的权限等。

                    具体的在web应用中集成shiro的方法记录如下：
                    ①在web.xml中配置shiro的filter
                    <?xml version="1.0" encoding="UTF-8"?>
                    <web-app
                            xmlns="http://java.sun.com/xml/ns/javaee"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="http://java.sun.com/xml/ns/javaeehttp://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
                            version="3.0"
                            metadata-complete="false">

                        <display-name>shiro test</display-name>

                        <listener>
                            <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
                        </listener>
                        <context-param>
                            <param-name>shiroEnvironmentClass</param-name>
                            <param-value>
                                org.apache.shiro.web.env.IniWebEnvironment
                            </param-value>
                        </context-param>
                        <context-param>
                            <param-name>shiroConfigLocations</param-name>
                            <param-value>classpath:shiro.ini</param-value>
                        </context-param>
                        <filter>
                            <filter-name>shiroFilter</filter-name>
                            <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
                        </filter>
                        <filter-mapping>
                            <filter-name>shiroFilter</filter-name>
                            <url-pattern>/*</url-pattern>
                        </filter-mapping>

                    </web-app>
                    其中标红的部分指定shiro的配置文件

                    ②配置shiro.ini配置文件
                    #测试用户
                    [users]
                    huqj=123,role1,role2
                    test=1234,role1,role3

                    [roles]
                    role1:user:insert,user:update
                    role2:user:update,user:delete
                    role3:user:query

                    #web集成配置需要鉴权授权的url pattern
                    [urls]
                    /login=anon
                    /personal=authc
                    /main=authc
                    /logout=authc
                    /role1=authc,roles[role1]
                    /role2=authc,roles[role2]
                    /permission=authc,perms["user:create"]
                    /logout2=logout

                    [main]
                    #没有登录的跳转页面
                    authc.loginUrl=/login
                    #没有角色的跳转页面
                    roles.unauthorizedUrl=/unauthorized
                    #没有权限的跳转页面
                    perms.unauthorizedUrl=/unauthorized
                    #不需要手动处理的退出，由shiro处理
                    logout.redirectUrl=/login
                    其中 users和roles和之前的se版本没什么区别，重点在main和urls的配置中。

                    其中urls配置相关的url请求是否需要验证、是否有角色要求、权限要求等等。例如：
                    /login=anon
                    表示/login请求不需要登录验证就可以访问，anon表示“匿名的”(anonymous)

                    /main=authc
                    表示/main请求（主页）需要登录验证才能访问authc表示authentication，如果有一个请求在没有登录验证的情况下访问，则由shiro filter处理，将其重定向到main中的
                    authc.loginUrl 指定的路径去（如果不指定则默认跳转到/login.jsp）。这点就省去了在业务逻辑中每次都要判断用户是否已经登录的麻烦。

                    /role1=authc,roles[role1]
                    表示/role1这个url必须要具有role1的用户登录验证之后才能访问，所以不具有role1角色的用户，即使登录了也无法访问，当出现这种情况时，会自动重定向到main中的roles.unauthorizedUrl指定的路径。如果不指定则会返回401未授权的状态码。

                    同样的
                    /permission=authc,perms["user:create"]
                    表示/permisson路径只有登陆了并且具有user:create权限的用户才能访问，否则跳转到main中perms.unauthorizationUrl指定的地址上去。

                    还有一个比较厉害的配置是main中的logout，
                    logout.redirectUrl=/login
                    这个表示登出之后重定向到/login即登录的页面去，结合urls中的
                    /logout2=logout
                    可以实现一键登出功能（只要在前端页面加上/logout2链接，就可以跳转到登录页面并且清除用户登录数据，不需要手动写后端逻辑）。

                    以上这些配置基本可以满足对权限要求不高甚至稍微有点高的web应用的需求了。我觉得还是很厉害的。

                    ③编写servlet接口
                    这里其实理解了上面两个配置就基本可以无需考虑的写后端接口了。只需要把有验证或者权限需求的接口url配置在shiro.ini文件中就可以认为能访问到该接口的都是验证通过或者有权限的用户了，不需要再在业务逻辑中考虑鉴权授权的问题了。
                    下面是一个简单的示例：
                    /**
                    * role1
                    *
                    * @author huqj
                    */
                    @WebServlet(name = "role1", urlPatterns = "/role1")
                    public class Role1Servlet extends HttpServlet {

                    @Override
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                    IOException {
                    Subject subject = SecurityUtils.getSubject();
                    req.setAttribute("username", subject.getPrincipal());
                    req.getRequestDispatcher("/WEB-INF/view/role1.jsp").forward(req, resp);
                    }
                    }

                    这里是用servlet3+jsp写的，转到spring其实也一样，后面会记录一下shiro与sprin集成的方法。


                    ===============================================================================
                    当然，除了上面这种方式，shiro与web应用集成还有两种方式：基于basic拦截器和基于表单拦截器（可以记住登录前的意图页面），具体可参加下文：
                    http://wiki.jikexueyuan.com/project/shiro/web-integration.html
                </code>
            </p>
            <small><b>标签：</b>java,shiro,鉴权</small>
        </div>
        <hr/>
        <div class="neiborhood">
            <br/>
            <b>上一篇:</b><a href="#">linux下安装zookeeper</a>
            <br/>
            <br/>
            <b>下一篇:</b><a href="#">hdfs完全分布式环境搭建</a>
            <br/>
            <br/>
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
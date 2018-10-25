<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Blog List</title>
    <link rel="stylesheet" href="../css/backStyle.css"/>
</head>
<body>
<div class="top-menu">
    <a target="_blank" href="../index"><img src="../image/logo.jpg" class="logo"/></a>
    <span class="top-right-menu">
        <a href="account"><button type="button" class="admin-account">Admin Account</button></a>
        <a href="logout"><button type="button" class="logout">Logout</button></a>
    </span>
</div>
<div class="top-menu-blank"></div>
<div class="whole">
    <div class="left-menu">
        <ul>
            <a href="console">
                <li>Overview</li>
            </a>
            <a href="blog">
                <li id="current">Blog</li>
            </a>
            <a href="essay">
                <li>Essay</li>
            </a>
            <a href="category">
                <li>Category</li>
            </a>
            <a href="publish">
                <li>Publish</li>
            </a>
            <a href="about">
                <li>About</li>
            </a>
            <a href="message">
                <li>Message</li>
            </a>
        </ul>
    </div>
    <div class="main-board">
        <h2>Blog List</h2>
        <hr/>
        <table class="essay-table">
            <tr>
                <th>id</th>
                <th>title</th>
                <th>scan num</th>
                <th>publish time</th>
                <th>update time</th>
                <th>operation</th>
            </tr>
            <c:forEach items="${blogList }" var="blog">
                <tr>
                    <td>${blog.id }</td>
                    <td><a href="../article?id=${blog.id }" target="_blank">${blog.title }</a></td>
                    <td>${blog.scanNum }</td>
                    <td>${blog.publishTimeStr }</td>
                    <td>${blog.updateTimeStr }</td>
                    <td>
                        <button type="button" class="op-delete op-btn" opid="${blog.id }">delete</button>
                        <button type="button" class="op-edit op-btn" opid="${blog.id }">edit</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <hr/>
        <%--分页--%>
        <div class="page">
            <ul id="pageUl">
            </ul>
        </div>
        <script type="application/javascript" src="../js/jquery.min.js"></script>
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
                //ajax取得总页面数目
                var totalPage = 1;
                $.ajax(
                    {
                        url: "../api/blog/page?back=true",
                        type: 'json',
                        method: "GET",
                        success: function (res) {
                            var result = eval(res);
                            totalPage = result.totalPage;
                            if (totalPage < curPage || curPage < 1) {  //当有人篡改page参数时
                                curPage = 1;
                            }
                            paintPageNavigator(curPage, totalPage);
                        },
                        error: function () {
                            console.error("error when send ajax request to get total page num.")
                            paintPageNavigator(curPage, 0);
                        }
                    }
                );

                //删除
                $(".op-delete").click(function () {
                    if (confirm("Be sure to delete this blog?")) {
                        var id = $(this).attr("opid");
                        window.location.href = "delete?type=blog&id=" + id;
                    }
                });
                //编辑
                $(".op-edit").click(function () {
                    var id = $(this).attr("opid");
                    window.location.href = "edit?type=blog&id=" + id;
                });
            });

            //画分页
            function paintPageNavigator(curPage, totalPage) {
                var delta = 1;  //当前页前后显示的页数，可调节
                var liStr = '<li class="wider"><a href="blog">首页</a></li>';
                if (curPage - delta > 2) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                for (var i = curPage - delta; i <= parseInt(curPage) + parseInt(delta); i++) {
                    if (i == curPage) {
                        liStr += '<li class="cur-page"><a href="#">' + curPage + '</a></li>';
                    } else if (i >= 1 && i <= totalPage) {
                        liStr += '<li><a href="blog?page=' + i + '">' + i + '</a></li>';
                    }
                }
                if (parseInt(curPage) + delta + 1 < totalPage) {
                    liStr += '<li><a href="#">...</a></li>';
                }
                liStr += '<li class="wider"><a href="blog?page=' + totalPage + '">尾页</a></li>';
                $("#pageUl").html(liStr);
            }

        </script>
    </div>
</div>
</body>
</html>

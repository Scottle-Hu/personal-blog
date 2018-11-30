package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Remark;
import top.huqj.blog.model.UserInfo;
import top.huqj.blog.service.IBlogService;
import top.huqj.blog.service.IEssayService;
import top.huqj.blog.service.IRemarkService;
import top.huqj.blog.service.IUserInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非页面跳转类的api，包括md转html等ajax接口
 *
 * @author huqj
 */
@RestController
@Log4j
@RequestMapping("/api")
public class ApiController {

    @Value("${maxBlogNumPerPage}")
    public int Blog_NUM_PER_PAGE;

    @Value("${maxEssayNumPerPage}")
    public int Essay_NUM_PER_PAGE;

    @Value("${backMaxBlogNumPerPage}")
    public int Back_Blog_NUM_PER_PAGE;

    @Value("${backMaxEssayNumPerPage}")
    public int Back_Essay_NUM_PER_PAGE;

    @Value("${maxImageSize}")
    public int Max_Image_Size;

    @Autowired
    private IBlogService blogService;

    @Autowired
    private IEssayService essayService;

    @Autowired
    private IRemarkService remarkService;

    @Autowired
    private IUserInfoService userInfoService;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyyMMdd");

    private String uploadRootPath =
            ApiController.class.getClassLoader().getResource("").getPath()
                    + "../../umeditor/jsp/upload/image/";

    @RequestMapping("/blog/page")
    public Object getTotalPage(HttpServletRequest request) {
        Map<String, Integer> result = new HashMap<>();
        try {
            String type = request.getParameter("type");
            //后台的分页设置和前台不一样
            boolean isBack = request.getParameter("back") != null;
            int totalBlogNum = -1;
            if (type == null) {  //所有博客
                totalBlogNum = blogService.count();
            } else if (type.equals(BlogConstant.TYPE_CATEGORY)) {  //按博客类别
                String categoryId = request.getParameter("categoryId");
                if (categoryId != null) {
                    totalBlogNum = blogService.countByCategoryId(Integer.parseInt(categoryId));
                }
            } else if (type.equals(BlogConstant.TYPE_MONTH)) {  //按照博客月份
                String month = request.getParameter("month");
                if (month != null) {
                    totalBlogNum = blogService.countByMonth(month);
                }
            } else {
                log.warn("unknown page type, type=" + type);
            }
            if (totalBlogNum != -1) {
                int num = isBack ? Back_Blog_NUM_PER_PAGE : Blog_NUM_PER_PAGE;
                result.put("totalPage", totalBlogNum / num + (totalBlogNum % num == 0 ? 0 : 1));
            }
        } catch (Exception e) {
            log.error("error when get count of blogs.", e);
        }
        return result;
    }

    @RequestMapping("/essay/page")
    public Object getEssayPage(HttpServletRequest request) {
        Map<String, Integer> result = new HashMap<>();
        try {
            String type = request.getParameter("type");
            //后台的分页设置和前台不一样
            boolean isBack = request.getParameter("back") != null;
            int totalEssayNum = -1;
            if (type == null) {  //所有随笔
                totalEssayNum = essayService.count();
            } else if (type.equals(BlogConstant.TYPE_MONTH)) {  //按随笔月份
                String month = request.getParameter("month");
                if (month != null) {
                    totalEssayNum = essayService.countByMonth(month);
                }
            } else {
                log.warn("unknown page type, type=" + type);
            }
            if (totalEssayNum != -1) {
                int num = isBack ? Back_Blog_NUM_PER_PAGE : Blog_NUM_PER_PAGE;
                result.put("totalPage", totalEssayNum / num + (totalEssayNum % num == 0 ? 0 : 1));
            }
        } catch (Exception e) {
            log.error("error when get count of essay.", e);
        }
        return result;
    }

    @RequestMapping("/blog/top/new")
    public void buildTopNewBlogHtml(HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(buildHtmlByBlogList(blogService.getTopNewBlogList()).getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            log.error("error when write top blogs.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error when close out.", e);
                }
            }
        }
    }

    @RequestMapping("/blog/top/scan")
    public void buildTopScanBlogHtml(HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(buildHtmlByBlogList(blogService.getTopScanBlogList()).getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            log.error("error when write top blogs.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error when close out.", e);
                }
            }
        }
    }

    @RequestMapping("/blog/top/remark")
    public void buildTopRemarkBlogHtml(HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(buildHtmlByBlogList(blogService.getTopRemarkBlogList()).getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            log.error("error when write top blogs.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error when close out.", e);
                }
            }
        }
    }

    @RequestMapping("/blog/recommend")
    public void buildRecommendBlogHtml(HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(buildHtmlByBlogList(blogService.getRecommendBlogList()).getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            log.error("error when write top blogs.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error when close out.", e);
                }
            }
        }
    }

    /**
     * 用于ajax获取某篇博文章的评论列表，以html格式返回
     *
     * @param request
     * @param response
     */
    @RequestMapping("/remarks")
    public void writeRemarksByArticle(HttpServletRequest request, HttpServletResponse response) {
        OutputStream out = null;
        try {
            String articleId = request.getParameter("articleId");
            if (!StringUtils.isEmpty(articleId)) {
                List<Remark> remarkList = remarkService.findByArticleId(Integer.parseInt(articleId));
                out = response.getOutputStream();
                out.write(buildRemarkListHtml(remarkList).getBytes("utf-8"));
                out.flush();
            }
        } catch (Exception e) {
            log.error("error find remarks.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error when close output stream.", e);
                }
            }
        }
    }

    /**
     * ajax上传图片
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Object uploadImage(@RequestParam("image") MultipartFile file) {
        Map<String, String> res = new HashMap<>();
        OutputStream out = null;
        InputStream in = null;
        try {
            //判断图片大小是否超标
            if (file.getSize() > Max_Image_Size) {
                res.put("status", "error");
                res.put("reason", "图片过大，无法上传！");
                return res;
            }
            //写文件
            in = file.getInputStream();
            String date = dirDateFormat.format(new Date());
            String suffix = date + "/" + System.currentTimeMillis() + ".png";
            String path = uploadRootPath + suffix;
            File parentDir = new File(uploadRootPath + "/" + date);
            if (!parentDir.exists()) {
                parentDir.mkdir();
            }
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            res.put("path", "/umeditor/jsp/upload/image/" + suffix);
            res.put("status", "success");
        } catch (Exception e) {
            log.error("error upload image.", e);
            res.put("status", "error");
            res.put("reason", "无法上传图片");
        } finally {  //关闭输入输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("error close output stream.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    log.error("error close input stream.");
                }
            }
        }
        return res;
    }

    /**
     * 根据查询出来的博客列表画出html标签并返回给ajax请求
     * 示例见下面的注释
     *
     * @param blogList
     * @return
     */
    /*
     <li>
        <a href="article?id=${blog.id }">
            <div class="blog">
                <h3>${blog.title }</h3>
                <p>${blog.text }</p>
                <div class="m-blog-img">
                    <c:forEach items="${blog.imgUrls }" var="img">
                        <img src="${img }"/>
                    </c:forEach>
                </div>
                <hr/>
                <small>${blog.publishTimeStr }</small>
                <small style="float:right;">浏览（${blog.scanNum }）</small>
            </div>
            <div class="blog-split"></div>
        </a>
    </li>
     */
    private String buildHtmlByBlogList(List<Blog> blogList) {
        StringBuilder result = new StringBuilder();
        for (Blog blog : blogList) {
            result.append("<li><a href=\"article?id=");
            result.append(blog.getId());
            result.append("\"><div class=\"blog\"><h3>");
            result.append(blog.getTitle());
            result.append("</h3><p>");
            result.append(blog.getText());
            result.append("</p><div class=\"m-blog-img\">");
            if (!CollectionUtils.isEmpty(blog.getImgUrls())) {
                for (String imgUrl : blog.getImgUrls()) {
                    result.append("<img src=\"");
                    result.append(imgUrl);
                    result.append("\"/>");
                }
            }
            result.append("</div><hr/><small>");
            result.append(blog.getPublishTimeStr());
            result.append("</small><small style=\"float:right;\">浏览（");
            result.append(blog.getScanNum());
            result.append("）</small></div><div class=\"blog-split\"/></a></li>\n");
        }
        return result.toString();
    }

    /**
     * 根据某篇文章的评论列表画出html代码
     *
     * @param remarkList
     * @return
     */
    /*
    <li>
        <a href="http://www.baidu.com" target="_blank">
            <img src="image/github_icon.jpg"/>&nbsp;&nbsp;Scottle-Hu
        </a>
        &nbsp;&nbsp;
        <small>2018-11-20 12:34:34</small>
        <br/>
        <div>好文章！赞一个~~测试测试钱钱钱钱钱大幅上涨的gasgas</div>
    </li>
     */
    private String buildRemarkListHtml(List<Remark> remarkList) {
        StringBuilder result = new StringBuilder();
        remarkList.forEach(remark -> {
            UserInfo userInfo = userInfoService.findById(remark.getObserverId());
            if (userInfo != null) {
                result.append("<li>");
                result.append("<a href=\"");
                result.append(userInfo.get_3rdPartyHomeUrl());
                result.append("\" target=\"_blank\"><img src=\"");
                result.append(userInfo.getIconUrl());
                result.append("\" />&nbsp;&nbsp;");
                result.append(userInfo.getUsername());
                result.append("</a>&nbsp;&nbsp;<small>");
                result.append(dateFormat.format(remark.getPublishTime()));
                result.append("</small><br/><div>");
                result.append(remark.getContent());
                result.append("</div>");
                result.append("</li>");
            }
        });
        return result.toString();
    }

}

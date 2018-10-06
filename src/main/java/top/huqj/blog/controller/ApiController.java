package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;
import top.huqj.blog.service.IBlogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    @Autowired
    private IBlogService blogService;

    @RequestMapping("/blog/page")
    public Object getTotalPage(HttpServletRequest request) {
        Map<String, Integer> result = new HashMap<>();
        try {
            String type = request.getParameter("type");
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
                month = URLDecoder.decode(month, "iso-8859-1");  //由于是从url中截取的参数，会做url编码，这里需要解码
                month = new String(month.getBytes("iso-8859-1"), "utf-8");
                if (month != null) {
                    totalBlogNum = blogService.countByMonth(month);
                }
            } else {
                log.warn("unknown page type, type=" + type);
            }
            if (totalBlogNum != -1) {
                result.put("totalPage", totalBlogNum % Blog_NUM_PER_PAGE == 0 ?
                        totalBlogNum / Blog_NUM_PER_PAGE : (totalBlogNum / Blog_NUM_PER_PAGE) + 1);
            }
        } catch (Exception e) {
            log.error("error when get count of blogs.", e);
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
     * 根据查询出来的博客列表画出html标签并返回给ajax请求
     * 示例见下面的注释
     *
     * @param blogList
     * @return
     */
    /*
     <li>
     <a href="#">
     <div class="blog">
     <h4>使用vmware搭建centos虚拟机集群记录</h4>
     <p>因为课程任务需要搭建服务器集群，然后尝试分布式程序来进行聚类分析......</p>
     <img src="image/logo.jpg"/>
     <img src="image/list_icon.png"/>
     <br/><br/>
     <small>2018-03-21 10:34</small>
     <small style="float:right;">浏览（123）</small>
     <hr/>
     </div>
     </a>
     </li>
     */
    private String buildHtmlByBlogList(List<Blog> blogList) {
        StringBuilder result = new StringBuilder();
        for (Blog blog : blogList) {
            result.append("<li><a href=\"article?id=");
            result.append(blog.getId());
            result.append("\"><div class=\"blog\"><h4>");
            result.append(blog.getTitle());
            result.append("</h4><p>");
            result.append(blog.getText());
            result.append("</p>");
            if (!CollectionUtils.isEmpty(blog.getImgUrls())) {
                for (String imgUrl : blog.getImgUrls()) {
                    result.append("<img src=\"");
                    result.append(imgUrl);
                    result.append("\"/>");
                }
            }
            result.append("<br/><br/><small>");
            result.append(blog.getPublishTimeStr());
            result.append("</small><small style=\"float:right;\">浏览（");
            result.append(blog.getScanNum());
            result.append("）</small><hr/></div></a></li>\n");
        }
        return result.toString();
    }

}

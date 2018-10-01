package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.service.IBlogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主控制器
 *
 * @author huqj
 */
@Controller
@Log4j
public class BlogController {

    @Autowired
    private IBlogService blogService;

    public static int ARTICLE_NUM_PER_PAGE = 10;

    @RequestMapping("/")
    public String homePage(HttpServletRequest request) {
        return indexPage(request);
    }

    @RequestMapping("/index")
    public String indexPage(HttpServletRequest request) {
        try {
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            //根据分页信息获取最新博客列表，一页最多10篇
            Map<String, Integer> pageInfo = new HashMap<>();
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * ARTICLE_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, ARTICLE_NUM_PER_PAGE);
            List<Blog> blogList = blogService.findLatestBlogByPage(pageInfo);
            request.setAttribute("blogList", blogList);

            //获取所有博客类别和对应的博客数量
            request.setAttribute("categoryList", blogService.getAllCategoryList());

            //获取所有有博客的月份以及对应的博客篇数
            request.setAttribute("monthList", blogService.getAllMonthList());

        } catch (Exception e) {
            log.error("error when set index page.", e);
        }

        return "index";
    }

    @RequestMapping("/blog")
    public String blogPage(HttpServletRequest request) {
        //TODO
        return "blog";
    }

    @RequestMapping("/essay")
    public String essayPage(HttpServletRequest request) {
        //TODO
        return "essay";
    }

    @RequestMapping("/contact")
    public String contactPage(HttpServletRequest request) {
        //TODO
        return "contact";
    }

    @RequestMapping("/about")
    public String aboutPage(HttpServletRequest request) {
        //TODO
        return "about";
    }

    @RequestMapping("/article")
    public String articlePage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String articleId = request.getParameter("id");
            String type = request.getParameter("type");
            int id = -1;
            try {
                id = Integer.parseInt(articleId);
            } catch (NumberFormatException e) {
                log.error("error when parse article id.", e);
            }
            if (id == -1) {
                return gotoHome();
            }
            if (type == null || BlogConstant.BLOG_TYPE.equals(type)) {  //是博客
                Blog blog = blogService.findBlogById(id);
                if (blog == null) {
                    return gotoHome();
                }
                request.setAttribute("blog", blog);
                request.setAttribute("categoryList", blogService.getAllCategoryList());
                request.setAttribute("monthList", blogService.getAllMonthList());
                request.setAttribute("type", BlogConstant.BLOG_TYPE_ID);
            } else if (BlogConstant.ESSAY_TYPE.equals(type)) {  //是随笔
                //TODO
            } else {
                return gotoHome();
            }

        } catch (Exception e) {
            log.error("error when set article page.", e);
        }
        return "article";
    }

    /**
     * 用于跳转到home
     *
     * @return
     */
    private String gotoHome() {
        return "redirect:index";
    }

}

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
import top.huqj.blog.service.IBlogService;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CategoryDao categoryDao;

    private int ARTICLE_NUM_PER_PAGE = 10;

    @RequestMapping("/")
    public String homePage(HttpServletRequest request) {
        return indexPage(request);
    }

    @RequestMapping("/index")
    public String indexPage(HttpServletRequest request) {
        String pageStr = request.getParameter("page");
        int page = 1;
        if (!StringUtils.isEmpty(pageStr)) {
            page = Integer.parseInt(pageStr);
        }
        //根据分页信息获取最新博客列表，一页最多10篇
        Map<String, Integer> pageInfo = new HashMap<>();
        pageInfo.put(BlogConstant.PAGE_NO, page);
        pageInfo.put(BlogConstant.PAGE_NUM, ARTICLE_NUM_PER_PAGE);
        List<Blog> blogList = blogService.findLatestBlogByPage(pageInfo);
        request.setAttribute("blogList", blogList);

        //获取所有博客类别
        List<Category> categoryList = categoryDao.findAll();
        request.setAttribute("categoryList", categoryList);

        //获取所有有博客的月份以及对应的博客篇数

        return "index";
    }


}

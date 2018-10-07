package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.Essay;
import top.huqj.blog.service.IBlogService;
import top.huqj.blog.service.ICategoryService;
import top.huqj.blog.service.IEssayService;

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
    private ICategoryService categoryService;

    @Autowired
    private IEssayService essayService;

    @Value("${maxBlogNumPerPage}")
    public int Blog_NUM_PER_PAGE;

    @Value("${maxEssayNumPerPage}")
    public int Essay_NUM_PER_PAGE;

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
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, Blog_NUM_PER_PAGE);
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
        try {
            //获取所有博客类别和对应的博客数量
            request.setAttribute("categoryList", blogService.getAllCategoryList());
        } catch (Exception e) {
            log.error("error when set blog page.", e);
        }
        return "blog";
    }

    @RequestMapping("/essay")
    public String essayPage(HttpServletRequest request) {
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
            Map<String, Integer> pageInfo = new HashMap<>();
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * Essay_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, Essay_NUM_PER_PAGE);
            List<Essay> essayList = essayService.findLatestEssayListByPageInfo(pageInfo);
            request.setAttribute("essayList", essayList);
            request.setAttribute("monthList", essayService.getAllMonthAndEssayNum());
        } catch (Exception e) {
            log.error("error when set essay page.", e);
        }
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
    public String articlePage(HttpServletRequest request) {
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
            if (type == null || BlogConstant.BLOG_TYPE.equals(type)) {  //博客
                Blog blog = blogService.findBlogById(id);
                if (blog == null) {
                    return gotoHome();
                }
                request.setAttribute("blog", blog);
                request.setAttribute("categoryList", blogService.getAllCategoryList());
                request.setAttribute("monthList", blogService.getAllMonthList());
                request.setAttribute("previousBlog", blogService.getPrevious(id));
                request.setAttribute("nextBlog", blogService.getNext(id));
                request.setAttribute("type", BlogConstant.BLOG_TYPE_ID);
            } else if (BlogConstant.ESSAY_TYPE.equals(type)) {  //随笔
                Essay essay = essayService.findById(id);
                essayService.scanOnce(id);  //浏览量++
                if (essay == null) {
                    return gotoHome();
                }
                request.setAttribute("essay", essay);
                request.setAttribute("previousEssay", essayService.getPreviousEssay(id));
                request.setAttribute("nextEssay", essayService.getNextEssay(id));
                request.setAttribute("monthList", essayService.getAllMonthAndEssayNum());
                request.setAttribute("type", BlogConstant.ESSAY_TYPE_ID);
            } else {
                return gotoHome();
            }

        } catch (Exception e) {
            log.error("error when set article page.", e);
        }
        return "article";
    }

    @RequestMapping("/category")
    public String categoryPage(HttpServletRequest request) {
        try {
            String categoryId = request.getParameter("id");
            if (StringUtils.isEmpty(categoryId)) {  //没有id参数
                return gotoHome();
            }
            Category category = null;
            int cid = -1;
            try {
                cid = Integer.parseInt(categoryId);
                category = categoryService.findCategoryById(cid);
            } catch (NumberFormatException e) {
                log.error("error when parse category id.", e);
                return gotoHome();
            }
            if (category == null) {
                log.error("no such category, id=" + categoryId);
                return gotoHome();
            }
            request.setAttribute("category", category);
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            //根据类别和分页信息获取最新博客列表，一页最多10篇
            Map<String, Integer> pageInfo = new HashMap<>();
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.TYPE_CATEGORY, cid);
            List<Blog> blogList = blogService.findLatestBlogByPageAndCategory(pageInfo);
            request.setAttribute("blogList", blogList);

            //获取所有博客类别和对应的博客数量
            request.setAttribute("categoryList", blogService.getAllCategoryList());
            //获取所有有博客的月份以及对应的博客篇数
            request.setAttribute("monthList", blogService.getAllMonthList());

        } catch (Exception e) {
            log.error("error when set category page.", e);
        }
        return "category";
    }

    @RequestMapping("/month")
    public String monthBlogPage(HttpServletRequest request) {
        try {
            String month = request.getParameter("period");
            //转换中文编码格式
            month = new String(month.getBytes("iso-8859-1"), "utf-8");
            if (StringUtils.isEmpty(month)) {
                return gotoHome();
            }
            request.setAttribute("month", month);
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            //根据类别和分页信息获取最新博客列表，一页最多10篇
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.TYPE_MONTH, month);
            List<Blog> blogList = blogService.findLatestBlogByPageAndMonth(pageInfo);
            request.setAttribute("blogList", blogList);

            //获取所有博客类别和对应的博客数量
            request.setAttribute("categoryList", blogService.getAllCategoryList());
            //获取所有有博客的月份以及对应的博客篇数
            request.setAttribute("monthList", blogService.getAllMonthList());

        } catch (Exception e) {
            log.error("error when set month-blog page.", e);
        }
        return "month-blog";
    }

    @RequestMapping("/monthessay")
    public String monthEssayPage(HttpServletRequest request) {
        try {
            String month = request.getParameter("period");
            //转换中文编码格式
            month = new String(month.getBytes("iso-8859-1"), "utf-8");
            if (StringUtils.isEmpty(month)) {
                return gotoHome();
            }
            request.setAttribute("month", month);
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put(BlogConstant.PAGE_OFFSET, (page - 1) * Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.PAGE_NUM, Blog_NUM_PER_PAGE);
            pageInfo.put(BlogConstant.TYPE_MONTH, month);
            List<Essay> essayList = essayService.findLatestEssayListByPageInfoAndMonth(pageInfo);
            request.setAttribute("essayList", essayList);
            request.setAttribute("monthList", essayService.getAllMonthAndEssayNum());

        } catch (Exception e) {
            log.error("error when set month-blog page.", e);
        }
        return "month-essay";
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

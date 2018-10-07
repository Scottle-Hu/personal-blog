package top.huqj.blog.controller.back;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.exception.ParameterMissingException;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.Essay;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.service.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理主控制器
 *
 * @author huqj
 */
@Log4j
@Controller
@RequestMapping("/back")
public class BackController {

    @Value("${maxBlogNumPerPage}")
    public int Blog_NUM_PER_PAGE;

    @Value("${maxEssayNumPerPage}")
    public int Essay_NUM_PER_PAGE;

    @Autowired
    private IBlogService blogService;

    @Autowired
    private IEssayService essayService;

    @Autowired
    private IBlogIdProvider blogIdProvider;

    @Autowired
    private IEssayIdProvider essayIdProvider;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ICategoryService categoryService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int DATE_FORMAT_LENGTH = 19;

    @RequestMapping("/")
    public String defaultIndexPage(HttpServletRequest request) {
        return loginPage(request);
    }

    @RequestMapping("/login")
    public String loginPage(HttpServletRequest request) {
        return "back/login";
    }

    @RequestMapping("/check")
    public String checkLogin(HttpServletRequest request) {
        //验证错误会到此controller
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        if (!StringUtils.isEmpty(exceptionClassName)) {
            if (exceptionClassName.equals(UnknownAccountException.class.getName())) {
                request.setAttribute("errMsg", "用户不存在");
            } else if (exceptionClassName.equals(IncorrectCredentialsException.class.getName())) {
                request.setAttribute("errMsg", "密码错误");
            } else {
                request.setAttribute("errMsg", "未知登录错误");
            }
        }
        return "back/login";
    }

    @RequestMapping("/console")
    public String consolePage(HttpServletRequest request) {
        //TODO
        return "back/console";
    }

    /**
     * 前往发布文章的页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public String publishArticle(HttpServletRequest request) {
        List<Category> allCategoryList = categoryDao.findAll();
        request.setAttribute("categoryList", allCategoryList);
        return "back/publish";
    }

    /**
     * 提交发布的文章并返回文章列表页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public String publishArticlePost(HttpServletRequest request) {
        try {
            int contentType = BlogConstant.BLOG_TYPE_HTML;  //TODO 目前未实现markdown
            int articleType = Integer.parseInt(request.getParameter("type"));
            if (articleType == BlogConstant.BLOG_TYPE_ID) {
                Blog blog = new Blog();
                blog.setType(contentType);
                Date publishDate = new Date();
                String publishTimeStr = request.getParameter("publishTimeStr");
                if (!StringUtils.isEmpty(publishTimeStr) && publishTimeStr.length() == DATE_FORMAT_LENGTH) {  //形如 2018-10-02 18:55:00
                    try {
                        publishDate = dateFormat.parse(publishTimeStr);
                    } catch (ParseException e) {
                        log.error("error when parse publish time string. str=" + publishTimeStr, e);
                    }
                }
                blog.setPublishTime(new Timestamp(publishDate.getTime()));
                String categoryId = request.getParameter("categoryId");
                checkNotNull(categoryId);
                blog.setCategoryId(Integer.parseInt(categoryId));
                if (contentType == BlogConstant.BLOG_TYPE_HTML) {
                    String htmlContent = request.getParameter("htmlContent");
                    checkNotNull(htmlContent);
                    blog.setHtmlContent(htmlContent);
                    String text = request.getParameter("text");
                    checkNotNull(text);
                    blog.setText(text);
                } else if (contentType == BlogConstant.BLOG_TYPE_MD) {
                    String mdContent = request.getParameter("mdContent");
                    checkNotNull(mdContent);
                    blog.setMdContent(mdContent);
                } else {
                    log.warn("unknown content type: " + contentType);
                    request.setAttribute("errorMsg", "非法的文本编辑类型！");
                    return "back/publish";
                }
                String title = request.getParameter("title");
                checkNotNull(title);
                blog.setTitle(title);
                String tag = request.getParameter("tag");
                checkNotNull(tag);
                blog.setTag(tag);
                blog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                String recommend = request.getParameter("isRecommend");
                checkNotNull(recommend);
                blog.setRecommend(Boolean.parseBoolean(recommend));
                blog.setId(blogIdProvider.provideId());
                blogService.insertBlog(blog);
                request.setAttribute("msg", "发布成功！博客id " + blog.getId());
                return "redirect:blog";
            } else if (articleType == BlogConstant.ESSAY_TYPE_ID) {
                Essay essay = new Essay();
                essay.setType(contentType);
                Date publishDate = new Date();
                String publishTimeStr = request.getParameter("publishTimeStr-2");
                if (!StringUtils.isEmpty(publishTimeStr) && publishTimeStr.length() == DATE_FORMAT_LENGTH) {
                    try {
                        publishDate = dateFormat.parse(publishTimeStr);
                    } catch (ParseException e) {
                        log.error("error when parse publish time string. str=" + publishTimeStr, e);
                    }
                }
                essay.setPublishTime(new Timestamp(publishDate.getTime()));
                if (contentType == BlogConstant.BLOG_TYPE_HTML) {
                    String htmlContent = request.getParameter("htmlContent-2");
                    checkNotNull(htmlContent);
                    essay.setHtmlContent(htmlContent);
                    String text = request.getParameter("text-2");
                    checkNotNull(text);
                    essay.setText(text);
                } else if (contentType == BlogConstant.BLOG_TYPE_MD) {
                    String mdContent = request.getParameter("mdContent-2");
                    checkNotNull(mdContent);
                    essay.setMdContent(mdContent);
                } else {
                    log.warn("unknown content type: " + contentType);
                    request.setAttribute("errorMsg", "非法的文本编辑类型！");
                    return "back/publish";
                }
                String title = request.getParameter("title-2");
                checkNotNull(title);
                essay.setTitle(title);
                essay.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                essay.setId(essayIdProvider.provideId());
                essayService.insertOne(essay);
                return "redirect:essay";
            } else {
                log.warn("unknown article type: " + articleType);
                request.setAttribute("errorMsg", "非法的文章类型！");
                return publishArticle(request);
            }
        } catch (Exception e) {
            log.error("error when publish article.", e);
            request.setAttribute("errorMsg", "发布文章时发生错误，发布失败！");
            return publishArticle(request);
        }
    }

    @RequestMapping("/essay")
    public String essayListPage(HttpServletRequest request) {
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
        } catch (Exception e) {
            log.error("error when set essay list page of back.", e);
        }
        return "back/essay";
    }

    @RequestMapping("/blog")
    public String blogListPage(HttpServletRequest request) {
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
            List<Blog> blogList = blogService.findLatestBlogByPage(pageInfo);
            request.setAttribute("blogList", blogList);
        } catch (Exception e) {
            log.error("error when set blog list page of back.", e);
        }
        return "back/blog";
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public String categoryPage(HttpServletRequest request) {
        try {
            List<CategoryAndBlogNum> categoryList = categoryService.getAllCategoryList();
            request.setAttribute("categoryList", categoryList);
        } catch (Exception e) {
            log.error("error when set category page of back.", e);
        }
        return "back/category";
    }

    /**
     * 添加博客类别
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/category", method = RequestMethod.POST)
    public String addCategory(HttpServletRequest request) {
        try {
            String name = request.getParameter("name");
            if (!StringUtils.isEmpty(name)) {
                Category category = new Category();
                category.setCreateTime(new java.sql.Date(System.currentTimeMillis()));
                category.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
                category.setName(name);
                categoryService.addCategory(category);
            }
        } catch (Exception e) {
            log.error("error when add category.", e);
        }
        return categoryPage(request);
    }

    @RequestMapping("/delete")
    public String deleteBlogOrEssay(HttpServletRequest request) {
        try {
            String type = request.getParameter("type");
            String idStr = request.getParameter("id");
            if (StringUtils.isEmpty(idStr)) {
                throw new Exception();
            }
            int id = Integer.parseInt(idStr);
            if (type == null || type.equals(BlogConstant.BLOG_TYPE)) {
                blogService.deleteBlog(id);
                return "redirect:blog";
            } else if (type.equals(BlogConstant.ESSAY_TYPE)) {
                essayService.deleteById(id);
                return "redirect:essay";
            } else if (type.equals(BlogConstant.TYPE_CATEGORY)) {
                categoryService.deleteCategory(id);
                return "redirect:category";
            } else {
                log.warn("unknown type, type=" + type);
            }
        } catch (Exception e) {
            log.error("error when delete article.", e);
        }
        return "back/login";
    }

    /**
     * 检查必要参数是否为空，若是则抛出异常
     *
     * @param t
     * @param <T>
     * @throws ParameterMissingException
     */
    private <T> void checkNotNull(T t) throws ParameterMissingException {
        if (t == null) {
            throw new ParameterMissingException("neccessary parameter missed.");
        }
    }

}

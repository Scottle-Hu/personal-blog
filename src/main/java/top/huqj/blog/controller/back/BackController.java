package top.huqj.blog.controller.back;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.exception.ParameterMissingException;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.service.IBlogIdProvider;
import top.huqj.blog.service.IBlogService;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 后台管理主控制器
 *
 * @author huqj
 */
@Log4j
@Controller
@RequestMapping("/back")
public class BackController {

    @Autowired
    private IBlogService blogService;

    @Autowired
    private IBlogIdProvider blogIdProvider;

    @Autowired
    private CategoryDao categoryDao;

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
                return "redirect:blogs";
            } else if (articleType == BlogConstant.ESSAY_TYPE_ID) {
                //TODO
                return "redirect:essays";
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

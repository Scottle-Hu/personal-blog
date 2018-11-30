package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.exception.ParameterMissingException;
import top.huqj.blog.model.*;
import top.huqj.blog.service.*;
import top.huqj.blog.service.impl.RedisManager;
import top.huqj.blog.service.lucene.Searcher;
import top.huqj.blog.utils.AnsjUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IRemarkService remarkService;

    @Autowired
    private Searcher luceneSearcher;

    @Value("${maxBlogNumPerPage}")
    public int Blog_NUM_PER_PAGE;

    @Value("${maxEssayNumPerPage}")
    public int Essay_NUM_PER_PAGE;

    private static final int MAX_PREVIEW_IMG_NUM = 1;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Set<String> cannotReplace = new HashSet<>();

    //触发ansj初始化，设置不能替换的字符串(标签冲突)<span class="search-key"></span>
    @PostConstruct
    private void initAnsj() {
        AnsjUtil.segment("");
        cannotReplace.add(" ");
        cannotReplace.add("span");
        cannotReplace.add("class");
        cannotReplace.add("=");
        cannotReplace.add("search");
        cannotReplace.add("key");
        cannotReplace.add("<");
        cannotReplace.add(">");
        cannotReplace.add("/");
        cannotReplace.add("-");
    }

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
            if (page <= 0) {
                page = 1;
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
            if (page <= 0) {
                page = 1;
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

    @RequestMapping("/share")
    public String sharePage(HttpServletRequest request) {
        //TODO
        return "share";
    }

    @RequestMapping("/article")
    public String articlePage(HttpServletRequest request) {
        try {
            findUserInfoFromCookie(request).ifPresent(userInfo -> {
                request.setAttribute("userInfo", userInfo);
            });
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
                blogService.scanOnce(id);  //浏览量++
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
            if (page <= 0) {
                page = 1;
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
            if (StringUtils.isEmpty(month)) {
                return gotoHome();
            }
            request.setAttribute("month", parseChineseNameOfMonth(month));
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            if (page <= 0) {
                page = 1;
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
            if (StringUtils.isEmpty(month)) {
                return gotoHome();
            }
            request.setAttribute("month", parseChineseNameOfMonth(month));
            String pageStr = request.getParameter("page");
            int page = 1;
            if (!StringUtils.isEmpty(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    log.error("error when parse param page.", e);
                }
            }
            if (page <= 0) {
                page = 1;
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
     * 发表评论
     *
     * @param request
     */
    @RequestMapping(value = "/remark", method = RequestMethod.POST)
    public void publishRemark(HttpServletRequest request, HttpServletResponse response) {
        OutputStream out = null;
        try {
            Optional<UserInfo> userInfoOptional = findUserInfoFromCookie(request);
            if (userInfoOptional.isPresent()) {
                String articleId = request.getParameter("articleId");
                checkNotNull(articleId);
                String content = request.getParameter("content");
                checkNotNull(content);
                Remark remark = new Remark();
                remark.setArticleId(Integer.parseInt(articleId));
                remark.setContent(content);
                remark.setObserverId(userInfoOptional.get().getId());
                remark.setPublishTime(new Timestamp(System.currentTimeMillis()));
                remarkService.insert(remark);
                out = response.getOutputStream();
                //给ajax回应，否则ajax以为是404!!
                out.write("ok".getBytes("utf-8"));
                out.flush();
            }
        } catch (Exception e) {
            log.error("error publish remark.", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("error close out.", e);
            }
        }
    }

    @RequestMapping("/search")
    public String searchResult(HttpServletRequest request) {
        try {
            String query = request.getParameter("q");
//            query = new String(query.getBytes("iso-8859-1"), "utf-8");
            if (!StringUtils.isEmpty(query)) {
                if (query.length() > 20) {  //限制搜索字符数
                    query = query.substring(0, 20);
                }
                long start = System.currentTimeMillis();
                List<Blog> blogs = luceneSearcher.topBlogIds(query);
                request.setAttribute("cost", (System.currentTimeMillis() - start));
                postProcessBlogList(blogs, query);
                //搜索结果
                request.setAttribute("blogList", blogs);
                request.setAttribute("resultNum", blogs.size());
                request.setAttribute("query", query);

                //侧边栏通用信息
                request.setAttribute("categoryList", blogService.getAllCategoryList());
                request.setAttribute("monthList", blogService.getAllMonthList());
            }
        } catch (Exception e) {
            log.error("error when search.", e);
            gotoHome();
        }
        return "search";
    }

    /**
     * 用于跳转到home
     *
     * @return
     */
    private String gotoHome() {
        return "redirect:index";
    }

    /**
     * 将数字形式的月份转换成中文格式
     *
     * @param month
     * @return
     */
    private String parseChineseNameOfMonth(String month) {
        if (month.charAt(4) == '0') {
            return month.substring(0, 4) + "年" + month.charAt(5) + "月";
        } else {
            return month.substring(0, 4) + "年" + month.substring(4, 6) + "月";
        }
    }

    /**
     * 根据cookie判断浏览者是否登录
     *
     * @param request
     * @return
     */
    private Optional<UserInfo> findUserInfoFromCookie(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        UserInfo userInfo = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            String cName = cookie.getName();
            if (cName.equals(BlogConstant.OAUTH_SESSION_ID)) {
                String userInfoId = redisManager.getString(cookie.getValue());
                if (userInfoId != null) {
                    userInfo = userInfoService.findById(userInfoId);
                }
            }
        }
        if (userInfo == null) {
            return Optional.empty();
        } else {
            return Optional.of(userInfo);
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

    /**
     * 设置日期字符串和图片列表
     *
     * @param blogList
     */
    private void postProcessBlogList(List<Blog> blogList, String query) {
        blogList.forEach(blog -> {
            blog.setPublishTimeStr(dateFormat.format(blog.getPublishTime()));
            blog.setUpdateTimeStr(dateFormat.format(blog.getUpdateTime()));
            blog.setTitle(blog.getTitle().replace("<", "&lt;").replace(">", "&gt;"));
            if (!StringUtils.isEmpty(blog.getImgUrlList())) {
                List<String> imgList = Arrays.asList(blog.getImgUrlList().split("\\|"));
                blog.setImgUrls(imgList.subList(0, Math.min(MAX_PREVIEW_IMG_NUM, imgList.size())));
            }
            //设置预览文字，不应该太长
            String pre = blog.getText();
            if (pre.length() > 100) {
                blog.setText(pre.substring(0, 100).replace("<", "&lt;").replace(">", "&gt;"));
            }
            Set<String> segment = AnsjUtil.segment(query);
            blog.setTitle(addHighlight(blog.getTitle(), segment));
            blog.setText(addHighlight(blog.getText(), segment));
        });
    }

    /**
     * 关键词高亮显示
     *
     * @param str
     * @return
     */
    private String addHighlight(String str, Set<String> segment) {
        boolean first = true;
        for (String k : segment) {
            if (!first && cannotReplace.contains(k)) {
                continue;
            }
            str = str.replace(k, "<span class=\"search-key\">" + k + "</span>");
            first = false;
        }
        return str;
    }

}

package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.exception.CategoryNotFoundException;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.model.ext.MonthAndBlogNum;
import top.huqj.blog.service.IBlogService;
import top.huqj.blog.utils.MarkDownUtil;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huqj
 */
@Service("blogService")
@Log4j
public class BlogServiceImpl implements IBlogService {

    @Autowired
    private BlogDao blogDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * 避免频繁count(*)
     * 记录博客数目的缓存,为了保险起见定期更新
     */
    private volatile int TOTAL_BLOG_NUM = -1;

    private static final long AN_HOUR_MILLIS = 60 * 60 * 1000;

    /**
     * 首页最多预览的图片数目
     */
    private static final int MAX_PREVIEW_IMG_NUM = 3;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void init() {
        //初始化博客数量
        TOTAL_BLOG_NUM = blogDao.count();
        log.info("init total blog num:" + TOTAL_BLOG_NUM);
        //定期更新
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TOTAL_BLOG_NUM = blogDao.count();
                log.info("update total blog num:" + TOTAL_BLOG_NUM);
            }
        }, AN_HOUR_MILLIS, AN_HOUR_MILLIS);
    }

    public Blog findBlogById(int id) {
        List<Blog> blogs = blogDao.findById(id);
        if (blogs.size() == 1) {
            Blog blog = blogs.get(0);
            //设置显示日期格式
            blog.setPublishTimeStr(dateFormat.format(blog.getPublishTime()));
            return blog;
        }
        return null;
    }

    @Override
    public void insertBlog(Blog blog) throws Exception {
        blog.setPublishTime(new Timestamp(System.currentTimeMillis()));
        blog.setUpdateTime(new Time(System.currentTimeMillis()));
        Category category = categoryDao.findById(blog.getCategoryId());
        //当找不到类别的时候抛出异常提示
        if (category == null) {
            log.error("can not find category by id when insert blog.");
            throw new CategoryNotFoundException("error: can not find category");
        }
        blog.setCategory(category);
        //如果是markdown语法写的博客，需要转换格式存储，如果使用ue编辑器会在前端提取text
        if (blog.getType() == BlogConstant.BLOG_TYPE_MD) {
            String md = blog.getMdContent();
            blog.setText(MarkDownUtil.md2text(md));
            blog.setHtmlContent(MarkDownUtil.md2html(md));
        }
        //提取图片链接
        blog.setImgUrlList(extractImgUrls(blog));

        blogDao.insertOne(blog);
    }

    @Override
    public List<Blog> findLatestBlogByPage(Map<String, Integer> page) {
        if (page == null || page.get(BlogConstant.PAGE_OFFSET) == null
                || page.get(BlogConstant.PAGE_NUM) == null) {  //参数错误
            return Collections.emptyList();
        }
        List<Blog> blogList = blogDao.findLatestByPage(page);
        if (CollectionUtils.isEmpty(blogList)) {  //没有博客列表，说明不是正常点击而是篡改了url，这时返回首页即可
            page.put(BlogConstant.PAGE_OFFSET, 0);
            blogList = blogDao.findLatestByPage(page);
        }
        //设置日期字符串和图片列表
        blogList.forEach(blog -> {
            blog.setPublishTimeStr(dateFormat.format(blog.getPublishTime()));
            if (!StringUtils.isEmpty(blog.getImgUrlList())) {
                List<String> imgList = Arrays.asList(blog.getImgUrlList().split("\\|"));
                blog.setImgUrls(imgList.subList(0, Math.min(MAX_PREVIEW_IMG_NUM, imgList.size())));
            }
        });
        return blogList;
    }

    @Override
    public int count() {
        if (TOTAL_BLOG_NUM < 0) {
            TOTAL_BLOG_NUM = blogDao.count();
        }
        return TOTAL_BLOG_NUM;
    }

    @Override
    public List<CategoryAndBlogNum> getAllCategoryList() {
        List<Category> categoryList = categoryDao.findAll();
        if (CollectionUtils.isEmpty(categoryList)) {
            return Collections.emptyList();
        }
        List<CategoryAndBlogNum> result = new ArrayList<>();
        for (Category category : categoryList) {
            result.add(new CategoryAndBlogNum(category, blogDao.countByCategoryId(category.getId())));
        }
        return result;
    }

    @Override
    public List<MonthAndBlogNum> getAllMonthList() {
        //TODO
        return null;
    }

    @Override
    public Blog getPrevious(int id) {
        return null;
    }

    @Override
    public Blog getNext(int id) {
        return null;
    }

    /**
     * 从表单提交的原始文本中提取图片链接并存储到blog中
     *
     * @param blog
     */
    private String extractImgUrls(Blog blog) {
        StringBuilder imgs = new StringBuilder();
        String html = blog.getHtmlContent();
        int index = html.indexOf("<img");
        while (index != -1) {
            int urlStart = html.indexOf("\"", index);
            if (urlStart == -1 || urlStart == html.length() - 1) {
                break;
            }
            int urlEnd = html.indexOf("\"", urlStart + 1);
            if (urlEnd == -1) {
                break;
            }
            imgs.append(html.substring(urlStart + 1, urlEnd));
            imgs.append("|");
            html = html.substring(urlEnd);
            index = html.indexOf("<img");
        }
        if (imgs.length() > 0) {
            imgs.deleteCharAt(imgs.length() - 1);
        }
        return imgs.toString().trim();
    }


}

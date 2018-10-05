package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.constant.BlogUpdateOperation;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.exception.CategoryNotFoundException;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.model.ext.MonthAndBlogNum;
import top.huqj.blog.service.IBlogIdProvider;
import top.huqj.blog.service.IBlogService;
import top.huqj.blog.utils.MarkDownUtil;

import javax.annotation.PostConstruct;
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

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private IBlogIdProvider blogIdProvider;

    /**
     * redis中存储最新博客id的列表名称
     */
    @Value("${redis.list.blog.top.new}")
    private String topNewsBlogIdListKey;

    /**
     * redis中存储浏览量最多的博客id的列表
     */
    @Value("${redis.list.blog.top.scan}")
    private String topScanBlogIdListKey;

    /**
     * redis中存储的评论最多的博客id列表
     */
    @Value("${redis.list.blog.top.remark}")
    private String topRemarkBlogIdListKey;

    /**
     * 博主推荐博客id集合
     */
    @Value("${redis.set.blog.recommend}")
    private String recommendBlogIdSetKey;

    /**
     * 一篇博客上下两篇博客id，中间用“-”分割
     */
    @Value("${redis.hash.blog.brothers}")
    private String brothersBlogIdHashKey;

    /**
     * 博客类别到博客id的对应关系列表名称的前缀，后面还需要加上类别id
     */
    @Value("${redis.list.blog.category2blog.prefix}")
    private String category2BlogIdsKeyPrefix;

    /**
     * 博客发布时间到博客id列表的名称前缀,后缀形如 ： 201809,201812
     */
    @Value("${redis.list.blog.month2blog.prefix}")
    private String month2BlogIdsKeyPrefix;

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

    private SimpleDateFormat monthDateFormat = new SimpleDateFormat("yyyyMMdd");

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
    public synchronized void insertBlog(Blog blog) throws Exception {
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
        //添加类别与博客id的对应
        redisManager.addList(category2BlogIdsKeyPrefix + blog.getCategoryId(), String.valueOf(blog.getId()));
        //添加时间与博客id的对应
        redisManager.addList(month2BlogIdsKeyPrefix + dateFormat.format(blog.getPublishTime()), String.valueOf(blog.getId()));
        //更新上下篇博客id对应关系
        updateBrotherBlog(blog.getId(), BlogUpdateOperation.ADD);
        //更新top博客


        blogDao.insertOne(blog);
    }

    /**
     * 添加和删除博客的状态下更新上下博客
     *
     * @param id
     * @param op
     */
    private void updateBrotherBlog(int id, BlogUpdateOperation op) {
        switch (op) {
            case ADD: {  //添加博客，只需要添加当前博客以及更新上一篇博客
                int preBlogId = id - 1;
                while (preBlogId > 0) {
                    Optional<String> preBlogIdValue = redisManager.getHashValueByKey(brothersBlogIdHashKey, String.valueOf(preBlogId));
                    if (preBlogIdValue.isPresent() && preBlogIdValue.get().endsWith("-0")) {  //上一篇博客应当没有下一篇id
                        redisManager.setHash(brothersBlogIdHashKey, String.valueOf(id), preBlogId + "-0");
                        String oldValue = preBlogIdValue.get();
                        redisManager.setHash(brothersBlogIdHashKey, String.valueOf(preBlogId),
                                oldValue.substring(0, oldValue.indexOf("-")) + "-" + id);
                        break;
                    }
                    preBlogId--;
                }
                if (preBlogId == 0) { //没找到上一篇博客，只可能在第一次添加博客时出现
                    log.warn("can not find previous blog, this can be legal only when insert the first blog.");
                    redisManager.setHash(brothersBlogIdHashKey, String.valueOf(id), "0-0");
                }
                break;
            }
            case DELETE: {  //删除博客，需要删除当前id，以及更新上下文
                Optional<String> brotherIds = redisManager.getHashValueByKey(brothersBlogIdHashKey, String.valueOf(id));
                if (brotherIds.isPresent()) {
                    redisManager.removeHashKey(brothersBlogIdHashKey, String.valueOf(id));
                    if ("0-0".equals(brotherIds)) {
                        break;
                    }
                    String[] brotherIdsValueStr = brotherIds.get().split("-");
                    if (brotherIdsValueStr.length != 2) {
                        log.error("brotherIdsValueStr is an illegal string: " + brotherIdsValueStr);
                        break;
                    }
                    Optional<String> nextId = redisManager.getHashValueByKey(brothersBlogIdHashKey, brotherIdsValueStr[1]);
                    if (nextId.isPresent()) {
                        String nextIdValue = nextId.get();
                        if (nextIdValue.contains("-")) {
                            nextIdValue = nextIdValue.substring(nextIdValue.indexOf("-") + 1, nextIdValue.length());
                        } else {
                            log.error("illegal brother id string, nextIdValue=" + nextIdValue);
                            break;
                        }
                        redisManager.setHash(brothersBlogIdHashKey, brotherIdsValueStr[1]
                                , brotherIdsValueStr[0] + "-" + nextIdValue);
                    }
                    Optional<String> preId = redisManager.getHashValueByKey(brothersBlogIdHashKey, brotherIdsValueStr[0]);
                    if (preId.isPresent()) {
                        String preIdValue = preId.get();
                        if (preIdValue.contains("-")) {
                            preIdValue = preIdValue.substring(0, preIdValue.indexOf("-"));
                        } else {
                            log.error("illegal brother id string, preIdValue=" + preIdValue);
                            break;
                        }
                        redisManager.setHash(brothersBlogIdHashKey, brotherIdsValueStr[0]
                                , preIdValue + "-" + brotherIdsValueStr[1]);
                    }
                } else {
                    log.error("try to delete a nonexistent blog id. id=" + id);
                }
                break;
            }
        }
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
            //从redis中读取该类别的博客数量
            int num = (int) redisManager.getListLength(category2BlogIdsKeyPrefix + category.getId());
            if (num > 0) {
                result.add(new CategoryAndBlogNum(category, num));
            }
        }
        return result;
    }

    @Override
    public List<MonthAndBlogNum> getAllMonthList() {
        Set<String> month2BlogKeys = redisManager.listKeysByPrefix(month2BlogIdsKeyPrefix);
        List<MonthAndBlogNum> result = new ArrayList<>();
        month2BlogKeys.forEach(e -> {
            int num = (int) redisManager.getListLength(e);
            if (num > 0) {
                String monthName = e.substring(e.indexOf("-") + 1, e.length());
                if (monthName.length() == 6) {
                    monthName = parseChineseNameOfMonth(monthName);
                    result.add(new MonthAndBlogNum(monthName, num));
                }
            }
        });
        return result;
    }

    private String parseChineseNameOfMonth(String month) {
        if (month.charAt(4) == '0') {
            return month.substring(0, 4) + "年" + month.charAt(5) + "月";
        } else {
            return month.substring(0, 4) + "年" + month.substring(4, 6) + "月";
        }
    }

    @Override
    public Blog getPrevious(int id) {
        return getBrother(id, 0);
    }

    @Override
    public Blog getNext(int id) {
        return getBrother(id, 1);
    }

    /**
     * 根据博客id获取上下两篇博客，0代表上一篇，1代表下一篇
     *
     * @param id
     * @param previousOrNext
     * @return
     */
    private Blog getBrother(int id, int previousOrNext) {
        Optional<String> brotherIds = redisManager.getHashValueByKey(brothersBlogIdHashKey, String.valueOf(id));
        Blog blog = null;
        if (brotherIds.isPresent()) {
            String[] split = brotherIds.get().split("-");
            if (split.length != 2) {
                log.error("the brother ids of {" + id + "} is not equals two.");
            } else {
                try {
                    blog = findBlogById(Integer.parseInt(split[previousOrNext]));
                } catch (Exception e) {
                    log.error("error when parse brother blogs' id.", e);
                }
            }
        }
        return blog;
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

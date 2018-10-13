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

    /**
     * 浏览最多的列表中的最少浏览量
     */
    private volatile int minTopScanNum = 0;

    public static final long AN_HOUR_MILLIS = 60 * 60 * 1000;

    /**
     * 首页最多预览的图片数目
     */
    private static final int MAX_PREVIEW_IMG_NUM = 1;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat monthDateFormat = new SimpleDateFormat("yyyyMM");

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
        try {
            List<String> listValues = redisManager.getListValues(topScanBlogIdListKey);
            if (!CollectionUtils.isEmpty(listValues)) {
                minTopScanNum = Integer.parseInt(listValues.get(listValues.size() - 1).split("-")[1]);
            }
            log.info("init minTopScanNum as " + minTopScanNum);
        } catch (Exception e) {
            log.error("error when init minTopScanNum.", e);
        }
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
        redisManager.addListOnHead(category2BlogIdsKeyPrefix + blog.getCategoryId(), String.valueOf(blog.getId()));
        //添加时间与博客id的对应
        redisManager.addListOnHead(month2BlogIdsKeyPrefix + monthDateFormat.format(blog.getPublishTime()), String.valueOf(blog.getId()));
        //更新上下篇博客id对应关系
        updateBrotherBlog(blog.getId(), BlogUpdateOperation.ADD);
        //更新top博客
        updateTopBlog(blog, BlogUpdateOperation.ADD);

        blogDao.insertOne(blog);
        TOTAL_BLOG_NUM++;
    }

    @Override
    public synchronized void deleteBlog(int id) {
        Blog blog = findBlogById(id);
        if (blog == null) {
            log.warn("try to delete an nonexistent blog, id=" + id);
            return;
        }
        //从数据库删除
        blogDao.deleteOne(id);
        //更新redis
        if (blog.getCategory() == null) {
            log.error("this blog has no category. id=" + id);
        } else {
            int categoryId = blog.getCategory().getId();
            String categoryKey = category2BlogIdsKeyPrefix + categoryId;
            List<String> categoryList = redisManager.getListValues(categoryKey);
            Iterator<String> iterator = categoryList.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.equals(String.valueOf(id))) {
                    iterator.remove();
                }
            }
            redisManager.deleteListAllValue(categoryKey);
            redisManager.addListValueBatch(categoryKey, categoryList);

            String month = monthDateFormat.format(blog.getPublishTime());
            String monthKey = month2BlogIdsKeyPrefix + month;
            List<String> monthList = redisManager.getListValues(monthKey);
            iterator = monthList.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.equals(String.valueOf(id))) {
                    iterator.remove();
                }
            }
            redisManager.deleteListAllValue(monthKey);
            redisManager.addListValueBatch(monthKey, monthList);
        }

        updateBrotherBlog(id, BlogUpdateOperation.DELETE);
        updateTopBlog(blog, BlogUpdateOperation.DELETE);
        TOTAL_BLOG_NUM--;
    }

    /**
     * 更新各个维度的top博客列表，添加、删除、修改都可能变化
     * 每个纬度的列表默认最多十篇（博主推荐除外）
     *
     * @param blog
     * @param op
     */
    private void updateTopBlog(Blog blog, BlogUpdateOperation op) {
        String id = String.valueOf(blog.getId());
        switch (op) {
            case ADD: {
                if (blog.isRecommend()) {  //更新博主推荐
                    redisManager.addListOnHead(recommendBlogIdSetKey, String.valueOf(blog.getId()));
                }
                if (redisManager.getListLength(topRemarkBlogIdListKey) < 10) {  //更新最多评论
                    redisManager.addList(topRemarkBlogIdListKey, blog.getId() + "-0"); //默认0评论，添加在末尾
                }
                if (redisManager.getListLength(topScanBlogIdListKey) < 10) {  //更新最多浏览
                    redisManager.addList(topScanBlogIdListKey, blog.getId() + "-0"); //默认0浏览，添加在末尾
                    minTopScanNum = 0;
                }
                //更新最新博客
                while (redisManager.getListLength(topNewsBlogIdListKey) >= 10) {
                    redisManager.removeListTail(topNewsBlogIdListKey);
                }
                redisManager.addListOnHead(topNewsBlogIdListKey, String.valueOf(blog.getId()));
                break;
            }
            case DELETE: {  //注意：应该先从数据库中删除，再执行更新redis的操作
                //更新博主推荐
                List<String> recommendIds = redisManager.getListValues(recommendBlogIdSetKey);
                Iterator<String> iterator = recommendIds.iterator();
                while (iterator.hasNext()) {
                    String ele = iterator.next();
                    if (ele.equals(String.valueOf(blog.getId()))) {
                        iterator.remove();
                        break;
                    }
                }
                redisManager.deleteListAllValue(recommendBlogIdSetKey);
                redisManager.addListValueBatch(recommendBlogIdSetKey, recommendIds);
                //更新最新文章
                List<String> topNewBlogIds = redisManager.getListValues(topNewsBlogIdListKey);
                if (topNewBlogIds.contains(String.valueOf(blog.getId()))) {
                    List<String> candidateIds = blogDao.getTopNewBlog(10);
                    redisManager.deleteListAllValue(topNewsBlogIdListKey);
                    redisManager.addListValueBatch(topNewsBlogIdListKey, candidateIds);
                }
                //更新浏览最多
                List<String> topScanBlogIds = redisManager.getListValues(topScanBlogIdListKey);
                String mark = id + "-";
                if (hasValueStartWithAndRemoveOnce(topScanBlogIds, mark)) {
                    List<Blog> scanCandidates = blogDao.getTopScanBlog(topScanBlogIds.size() + 1);
                    if (!CollectionUtils.isEmpty(scanCandidates) && scanCandidates.size() > topScanBlogIds.size()) {
                        Blog newScanCandidate = scanCandidates.get(scanCandidates.size() - 1);
                        topScanBlogIds.add(newScanCandidate.getId() + "-" + newScanCandidate.getScanNum());
                        minTopScanNum = newScanCandidate.getScanNum();
                    } else if (topScanBlogIds.size() > 0) {
                        minTopScanNum = Integer.parseInt(topScanBlogIds.get(topScanBlogIds.size() - 1).split("-")[1]);
                    } else {
                        minTopScanNum = 0;
                    }
                    redisManager.deleteListAllValue(topScanBlogIdListKey);
                    redisManager.addListValueBatch(topScanBlogIdListKey, topScanBlogIds);
                }
                //更新浏览最多
                List<String> topRemarkBlogIds = redisManager.getListValues(topRemarkBlogIdListKey);
                if (hasValueStartWithAndRemoveOnce(topRemarkBlogIds, mark)) {
                    List<Blog> remarkCandidates = blogDao.getTopRemarkBlog(topRemarkBlogIds.size() + 1);
                    if (!CollectionUtils.isEmpty(remarkCandidates) && remarkCandidates.size() > topRemarkBlogIds.size()) {
                        Blog newRemarkCandidate = remarkCandidates.get(remarkCandidates.size() - 1);
                        topRemarkBlogIds.add(newRemarkCandidate.getId() + "-" + newRemarkCandidate.getScanNum());
                    }
                    redisManager.deleteListAllValue(topRemarkBlogIdListKey);
                    redisManager.addListValueBatch(topRemarkBlogIdListKey, topRemarkBlogIds);
                }
                break;
            }
            case UPDATE: {
                //TODO
                break;
            }
        }
    }

    /**
     * 在列表中查找有没有以prefix开头的字符串，如果有则删除并返回查找结果
     *
     * @param list
     * @param prefix
     * @return
     */
    private boolean hasValueStartWithAndRemoveOnce(List<String> list, String prefix) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String ele = it.next();
            if (ele.startsWith(prefix)) {
                it.remove();
                return true;
            }
        }
        return false;
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
                    if ("0-0".equals(brotherIds.get())) {
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
        postProcessBlogList(blogList);
        return blogList;
    }

    /**
     * 设置日期字符串和图片列表
     *
     * @param blogList
     */
    private void postProcessBlogList(List<Blog> blogList) {
        blogList.forEach(blog -> {
            blog.setPublishTimeStr(dateFormat.format(blog.getPublishTime()));
            blog.setUpdateTimeStr(dateFormat.format(blog.getUpdateTime()));
            if (!StringUtils.isEmpty(blog.getImgUrlList())) {
                List<String> imgList = Arrays.asList(blog.getImgUrlList().split("\\|"));
                blog.setImgUrls(imgList.subList(0, Math.min(MAX_PREVIEW_IMG_NUM, imgList.size())));
            }
            //设置预览文字，不应该太长
            String pre = blog.getText();
            if (pre.length() > 100) {
                blog.setText(pre.substring(0, 100));
            }
        });
    }

    /**
     * 根据博客类别和分页信息，从redis中获取博客列表
     *
     * @param page
     * @return
     */
    @Override
    public List<Blog> findLatestBlogByPageAndCategory(Map<String, Integer> page) {
        if (page == null || page.get(BlogConstant.PAGE_OFFSET) == null
                || page.get(BlogConstant.PAGE_NUM) == null || page.get(BlogConstant.TYPE_CATEGORY) == null) {
            return Collections.emptyList();
        }
        List<String> categoryBlogIds = redisManager
                .getListValues(category2BlogIdsKeyPrefix + page.get(BlogConstant.TYPE_CATEGORY));
        int offset = page.get(BlogConstant.PAGE_OFFSET), size = page.get(BlogConstant.PAGE_NUM);
        if (CollectionUtils.isEmpty(categoryBlogIds) || categoryBlogIds.size() <= offset) {
            return Collections.emptyList();
        }
        //注意：redis中 category2blog-xx 列表中的id更新时是按时间降序的，所以最新的在最前面
        categoryBlogIds = categoryBlogIds.subList(offset, Math.min(offset + size, categoryBlogIds.size()));
        List<Integer> idList = new ArrayList<>();
        categoryBlogIds.forEach(id -> {
            try {
                idList.add(Integer.parseInt(id));
            } catch (NumberFormatException e1) {
                log.error("error once when parse blog id.", e1);
            }
        });
        List<Blog> blogList = blogDao.findByIdList(idList);
        postProcessBlogList(blogList);
        return blogList;
    }

    @Override
    public List<Blog> findLatestBlogByPageAndMonth(Map<String, Object> page) {
        if (page == null || page.get(BlogConstant.PAGE_OFFSET) == null
                || page.get(BlogConstant.PAGE_NUM) == null || page.get(BlogConstant.TYPE_MONTH) == null) {
            return Collections.emptyList();
        }
        String monthSuffix = (String) page.get(BlogConstant.TYPE_MONTH);
        List<String> monthBlogIds = redisManager.getListValues(month2BlogIdsKeyPrefix + monthSuffix);
        int offset = (Integer) page.get(BlogConstant.PAGE_OFFSET), size = (Integer) page.get(BlogConstant.PAGE_NUM);
        if (CollectionUtils.isEmpty(monthBlogIds) || monthBlogIds.size() <= offset) {
            return Collections.emptyList();
        }
        monthBlogIds = monthBlogIds.subList(offset, Math.min(offset + size, monthBlogIds.size()));
        List<Integer> idList = new ArrayList<>();
        monthBlogIds.forEach(id -> {
            try {
                idList.add(Integer.parseInt(id));
            } catch (NumberFormatException e1) {
                log.error("error once when parse blog id.", e1);
            }
        });
        List<Blog> blogList = blogDao.findByIdList(idList);
        postProcessBlogList(blogList);
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
        //月份排序
        List<String> month2BlogKeysList = new ArrayList<>(month2BlogKeys);
        Collections.sort(month2BlogKeysList);
        List<MonthAndBlogNum> result = new ArrayList<>();
        month2BlogKeysList.forEach(e -> {
            int num = (int) redisManager.getListLength(e);
            if (num > 0) {
                String monthName = e.substring(e.indexOf("-") + 1, e.length());
                if (monthName.length() == 6) {
                    String chMonthStr = parseChineseNameOfMonth(monthName);
                    result.add(new MonthAndBlogNum(chMonthStr, monthName, num));
                }
            }
        });
        return result;
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
     * 将中文格式的月份转换成数字形式的字符串
     *
     * @param month
     * @return
     */
    private String reparseChineseNameOfMonth(String month) {
        String result = month = month.replace("年", "").replace("月", "");
        if (result.length() == 5) {  //月份小于10的情况,例如 20189
            result = result.substring(0, 4) + "0" + result.charAt(4);
        }
        if (result.length() != 6) {
            log.error("error when reparse chinese month string to num format. result=" + result);
            return month;  //返回原格式
        }
        return result;
    }

    @Override
    public Blog getPrevious(int id) {
        return getBrother(id, 0);
    }

    @Override
    public Blog getNext(int id) {
        return getBrother(id, 1);
    }

    @Override
    public int countByCategoryId(int id) {
        return (int) redisManager.getListLength(category2BlogIdsKeyPrefix + id);
    }

    @Override
    public int countByMonth(String month) {
        return (int) redisManager.getListLength(month2BlogIdsKeyPrefix + month);
    }

    @Override
    public List<Blog> getTopNewBlogList() {
        return getBlogListByRedisKey(topNewsBlogIdListKey);
    }

    @Override
    public List<Blog> getTopScanBlogList() {
        return getBlogListByRedisKey(topScanBlogIdListKey);
    }

    @Override
    public List<Blog> getTopRemarkBlogList() {
        return getBlogListByRedisKey(topRemarkBlogIdListKey);
    }

    @Override
    public List<Blog> getRecommendBlogList() {
        return getBlogListByRedisKey(recommendBlogIdSetKey);
    }

    /**
     * 需要加锁，不然多个线程同时执行会导致redis和数据库不同步
     *
     * @param id
     */
    @Override
    public synchronized void scanOnce(int id) {
        try {
            Blog blog = findBlogById(id);
            if (blog == null) {
                log.warn("scan nonexistent blog, id=" + id);
                return;
            }
            blogDao.scanOnce(id);
            //更新redis
            if (blog.getScanNum() >= minTopScanNum) {
                int nowScan = blog.getScanNum() + 1;
                String nowValue = id + "-" + nowScan;
                List<String> listValues = redisManager.getListValues(topScanBlogIdListKey);
                Iterator<String> iterator = listValues.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (Integer.parseInt(next.split("-")[1]) > nowScan) {
                        index++;
                    } else if (next.split("-")[0].equals(String.valueOf(id))) {
                        iterator.remove();
                        break;
                    }
                }
                List<String> newValues = new ArrayList<>();
                for (int i = 0; i < index; i++) {
                    newValues.add(listValues.get(i));
                }
                newValues.add(nowValue);
                for (int i = index; i < listValues.size(); i++) {
                    newValues.add(listValues.get(i));
                }
                if (newValues.size() > 10) {
                    newValues = newValues.subList(0, 10);
                }
                redisManager.deleteListAllValue(topScanBlogIdListKey);
                redisManager.addListValueBatch(topScanBlogIdListKey, newValues);
                //TODO top remark目前保持和 top scan 一致
                redisManager.deleteListAllValue(topRemarkBlogIdListKey);
                redisManager.addListValueBatch(topRemarkBlogIdListKey, newValues);
            }
        } catch (Exception e) {
            log.error("error when update scan num.", e);
        }
    }

    /**
     * 根据在redis中的列表key获取其对应的博客列表
     * 注意：有些列表的value并非博客id，而是 id-指标
     *
     * @param key
     * @return
     */
    private List<Blog> getBlogListByRedisKey(String key) {
        List<String> idExtList = redisManager.getListValues(key);
        List<Integer> ids = new ArrayList<>();
        idExtList.forEach(id -> {
            try {
                int segIndex = id.indexOf("-");
                if (segIndex != -1) {
                    id = id.substring(0, segIndex);
                }
                ids.add(Integer.parseInt(id));
            } catch (Exception e) {
                log.error("error when parse blog id.", e);
            }
        });
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        //不同的维度排序方式不同
        List<Blog> blogList;
        if (key.equals(topScanBlogIdListKey)) {
            blogList = blogDao.findByIdListOrderedScanNum(ids);
        } else if (key.equals(topRemarkBlogIdListKey)) {
            blogList = blogDao.findByIdListOrderedRemarkNum(ids);
        } else {
            blogList = blogDao.findByIdList(ids);
        }
        if (CollectionUtils.isEmpty(blogList)) {
            return Collections.emptyList();
        }
        postProcessBlogList(blogList);
        return blogList;
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
            int urlStart = html.indexOf("src=\"", index);
            if (urlStart == -1 || urlStart == html.length() - 1) {
                break;
            }
            int urlEnd = html.indexOf("\"", urlStart + 5);
            if (urlEnd == -1) {
                break;
            }
            imgs.append(html.substring(urlStart + 5, urlEnd));
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

package top.huqj.blog.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.dao.EssayDao;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.Essay;
import top.huqj.blog.service.impl.RedisManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 从数据库中提取出redis的内容，用于恢复redis，以及将线上数据迁移到测试环境
 *
 * @author huqj
 */
@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConvertDB2Redis {

    @Autowired
    private BlogDao blogDao;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private EssayDao essayDao;

    @Value("${redis.list.blog.top.new}")
    private String topNewsBlogIdListKey;

    @Value("${redis.list.blog.top.scan}")
    private String topScanBlogIdListKey;

    @Value("${redis.list.blog.top.remark}")
    private String topRemarkBlogIdListKey;

    @Value("${redis.hash.blog.brothers}")
    private String brothersBlogIdHashKey;

    @Value("${redis.list.blog.category2blog.prefix}")
    private String category2BlogIdsKeyPrefix;

    @Value("${redis.list.blog.month2blog.prefix}")
    private String month2BlogIdsKeyPrefix;

    @Value("${redis.hash.essay.brothers}")
    private String essayBrothersHashKey;

    @Value("${redis.list.essay.month2essay.prefix}")
    private String month2essayListKeyPrefix;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");


    /**
     * 根据数据库中的内容，创建redis中的缓存数据
     * 包括下面这些key：
     * blogTopNewList blogTopScanList blogTopRemarkList blogBrothersHash
     * category2blog-* month2blog-* month2essay-* essayBrothersHash
     * 注意在创建之前要删除key，并且推荐博客（）无法从数据库创建
     */
    @Test
    public void convertDB2Redist() {
        /*
        清除旧数据
         */
        redisManager.deleteListAllValue(topNewsBlogIdListKey);
        redisManager.deleteListAllValue(topRemarkBlogIdListKey);
        redisManager.deleteListAllValue(topScanBlogIdListKey);
        redisManager.listKeysByPrefix(category2BlogIdsKeyPrefix).forEach(key -> {
            redisManager.deleteListAllValue(key);
        });
        redisManager.listKeysByPrefix(month2BlogIdsKeyPrefix).forEach(key -> {
            redisManager.deleteListAllValue(key);
        });
        redisManager.listKeysByPrefix(month2essayListKeyPrefix).forEach(key -> {
            redisManager.deleteListAllValue(key);
        });
        redisManager.deleteListAllValue(brothersBlogIdHashKey);
        redisManager.deleteListAllValue(essayBrothersHashKey);

        //topNewsBlogIdListKey
        int TOP_NUM = 10;
        List<String> topNewBlog = blogDao.getTopNewBlog(TOP_NUM);
        redisManager.addListValueBatch(topNewsBlogIdListKey, topNewBlog);

        //topScanBlogIdListKey
        List<Blog> topScanBlog = blogDao.getTopScanBlog(TOP_NUM);
        List<String> topScanValues = topScanBlog.stream().map(blog -> {
            return blog.getId() + "-" + blog.getScanNum();
        }).collect(Collectors.toList());
        redisManager.addListValueBatch(topScanBlogIdListKey, topScanValues);

        //topRemarkBlogIdListKey
        //TODO top remark目前和top scan一样
        redisManager.addListValueBatch(topRemarkBlogIdListKey, topScanValues);

        //category2BlogIdsKeyPrefix
        List<Category> categoryList = categoryDao.findAll();
        for (Category category : categoryList) {
            int id = category.getId();
            List<String> blogIds = blogDao.findIdsByCategoryId(id);
            blogIds.forEach(bid -> {
                redisManager.addList(category2BlogIdsKeyPrefix + id, bid);
            });
        }

        //month2BlogIdsKeyPrefix
        List<Blog> allBlogs = blogDao.findLatestByPage(new HashMap<String, Integer>() {{
            this.put(BlogConstant.PAGE_OFFSET, 0);
            this.put(BlogConstant.PAGE_NUM, blogDao.count());
        }});
        Map<String, List<String>> month2Blog = new HashMap<>();
        allBlogs.forEach(blog -> {
            String month = dateFormat.format(blog.getPublishTime());
            if (month2Blog.get(month) == null) {
                month2Blog.put(month, new ArrayList<String>() {{
                    this.add(String.valueOf(blog.getId()));
                }});
            } else {
                month2Blog.get(month).add(String.valueOf(blog.getId()));
            }
        });
        month2Blog.forEach((k, v) -> {
            v.forEach(s -> {
                redisManager.addList(month2BlogIdsKeyPrefix + k, String.valueOf(s));
            });
        });

        //month2essayListKeyPrefix
        List<Essay> essayList = essayDao.findLatestByPageInfo(new HashMap<String, Integer>() {{
            this.put(BlogConstant.PAGE_OFFSET, 0);
            this.put(BlogConstant.PAGE_NUM, essayDao.count());
        }});
        Map<String, List<String>> month2Essay = new HashMap<>();
        essayList.forEach(essay -> {
            String month = dateFormat.format(essay.getPublishTime());
            if (month2Essay.get(month) == null) {
                month2Essay.put(month, new ArrayList<String>() {{
                    this.add(String.valueOf(essay.getId()));
                }});
            } else {
                month2Essay.get(month).add(String.valueOf(essay.getId()));
            }
        });
        month2Essay.forEach((k, v) -> {
            v.forEach(s -> {
                redisManager.addList(month2essayListKeyPrefix + k, String.valueOf(s));
            });
        });

        //brothersBlogIdHashKey
        if (!allBlogs.isEmpty()) {
            String firstValue = "-0";
            if (allBlogs.size() > 1) {
                firstValue = allBlogs.get(1).getId() + firstValue;
                String lastKey = allBlogs.get(allBlogs.size() - 1).getId() + "";
                String lastValue = "0-" + allBlogs.get(allBlogs.size() - 2).getId();
                redisManager.setHash(brothersBlogIdHashKey, lastKey, lastValue);
            } else {
                firstValue = "0-0";
            }
            redisManager.setHash(brothersBlogIdHashKey, String.valueOf(allBlogs.get(0).getId()), firstValue);
            for (int i = 1; i < allBlogs.size() - 1; i++) {
                String value = allBlogs.get(i + 1).getId() + "-" + allBlogs.get(i - 1).getId();
                redisManager.setHash(brothersBlogIdHashKey, String.valueOf(allBlogs.get(i).getId()), value);
            }
        }

        //essayBrothersHashKey
        if (!essayList.isEmpty()) {
            String firstValue = "-0";
            if (essayList.size() > 1) {
                firstValue = essayList.get(1).getId() + firstValue;
                String lastKey = essayList.get(essayList.size() - 1).getId() + "";
                String lastValue = "0-" + essayList.get(essayList.size() - 2).getId();
                redisManager.setHash(essayBrothersHashKey, lastKey, lastValue);
            } else {
                firstValue = "0-0";
            }
            redisManager.setHash(essayBrothersHashKey, String.valueOf(essayList.get(0).getId()), firstValue);
            for (int i = 1; i < essayList.size() - 1; i++) {
                String value = essayList.get(i + 1).getId() + "-" + essayList.get(i - 1).getId();
                redisManager.setHash(essayBrothersHashKey, String.valueOf(essayList.get(i).getId()), value);
            }
        }

    }

}

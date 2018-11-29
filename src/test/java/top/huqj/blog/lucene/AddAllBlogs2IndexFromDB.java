package top.huqj.blog.lucene;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;
import top.huqj.blog.service.IBlogService;
import top.huqj.blog.service.lucene.Indexer;
import top.huqj.blog.service.lucene.Searcher;

import java.util.HashMap;
import java.util.List;

/**
 * 直接从数据库将所有博客建立索引
 *
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class AddAllBlogs2IndexFromDB {

    @Autowired
    private Indexer indexer;

    @Autowired
    private Searcher searcher;

    @Autowired
    private IBlogService blogService;

    @Test
    public void testIndex() {
        final int count = blogService.count();
        System.out.println("count: " + count);
        List<Blog> blogList = blogService.findLatestBlogByPage(new HashMap<String, Integer>() {{
            put(BlogConstant.PAGE_OFFSET, 0);
            put(BlogConstant.PAGE_NUM, count);
        }});
        for (Blog blog : blogList) {
            indexer.addDocumentIndex(blog);
        }
    }

    /**
     * 用于建立索引之后测试
     */
    @Test
    public void testSearch() {
        List<Blog> blogs = searcher.topBlogIds("shiro授权");
        for (Blog blog : blogs) {
            System.out.println(blog.getTitle());
        }
    }
}

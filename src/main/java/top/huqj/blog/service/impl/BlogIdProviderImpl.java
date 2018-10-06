package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.model.Blog;
import top.huqj.blog.service.IBlogIdProvider;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author huqj
 */
@Log4j
@Service("blogIdProvider")
public class BlogIdProviderImpl implements IBlogIdProvider {

    /**
     * 记录当前最大的博客id
     */
    private volatile int id;

    @Autowired
    private BlogDao blogDao;

    @PostConstruct
    public void initMaxId() {
        List<Blog> maxIdBlog = blogDao.maxId();
        if (maxIdBlog.size() == 1) {
            id = maxIdBlog.get(0).getId();
        } else {
            log.info("no blog in db.");
            id = 0;
        }
        log.info("set blog max id as " + id);
    }

    /**
     * 生成一个博客id，需要加锁调用
     *
     * @return
     */
    @Override
    public synchronized int provideId() {
        return ++id;
    }

    /**
     * 查询当前最大id
     *
     * @return
     */
    @Override
    public synchronized int currentMaxId() {
        return id;
    }
}

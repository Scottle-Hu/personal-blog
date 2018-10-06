package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.dao.EssayDao;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Essay;
import top.huqj.blog.service.IEssayIdProvider;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author huqj
 */
@Log4j
@Service("essayIdProvider")
public class EssayIdProviderImpl implements IEssayIdProvider {

    /**
     * 记录当前最大的随笔id
     */
    private volatile int id;

    @Autowired
    private EssayDao essayDao;

    @PostConstruct
    public void initMaxId() {
        List<Essay> maxIdEssay = essayDao.maxId();
        if (maxIdEssay.size() == 1) {
            id = maxIdEssay.get(0).getId();
        } else {
            log.info("no essay in db.");
            id = 0;
        }
        log.info("set essay max id as " + id);
    }

    /**
     * 生成一个随笔id，需要加锁调用
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

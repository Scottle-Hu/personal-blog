package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.dao.EssayDao;

/**
 * 周期性用数据库数据更新redis数据的服务
 *
 * @author huqj
 */
@Service("updateRedisData")
@Log4j
public class UpdateRedisData {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private BlogDao blogDao;

    @Autowired
    private EssayDao essayDao;

    /**
     * 为了和数据库同步，更新redis的数据的方法
     */
    public void updateRedisData() {
        
    }
}

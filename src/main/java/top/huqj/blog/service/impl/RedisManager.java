package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 封装redis相关操作
 *
 * @author huqj
 */
@Service("redisManager")
@Log4j
public class RedisManager {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int port;

    private Jedis jedis;

    @PostConstruct
    private void init() {
        try {
            jedis = new Jedis(redisHost, port);
        } catch (Exception e) {
            log.error("error when connect to redis", e);
            //fast fail
            log.error("Application exited!");
            System.exit(1);
        }
    }

    public Set<String> listKeysByPrefix(String prefix) {
        if (prefix == null) {
            return Collections.emptySet();
        }
        return jedis.keys(prefix + "*");
    }

    public void set(String key, String value) {
        set(key, value, -1);
    }

    public void set(String key, String value, int expire) {
        jedis.set(key, value);
        if (expire > 0) {
            jedis.expire(key, expire);
        }
    }

    /**
     * 向列表末尾中添加元素
     *
     * @param listKey
     * @param value
     */
    public void addList(String listKey, String value) {
        jedis.lpush(listKey, value);
    }

    /**
     * 获取一个redis列表的所有值
     *
     * @param key
     * @return
     */
    public List<String> getListValues(String key) {
        Long len = jedis.llen(key);
        if (len == 0) {
            return Collections.emptyList();
        }
        return jedis.lrange(key, 0, len);
    }

    /**
     * 获取列表中指定偏移量的值
     *
     * @param key
     * @param index
     * @return
     */
    public Optional<String> getByListIndex(String key, int index) {
        Long len = jedis.llen(key);
        if (index < 0 || index >= len) {
            return Optional.empty();
        }
        return Optional.of(jedis.lindex(key, index));
    }

    public long getListLength(String key) {
        return jedis.llen(key);
    }

    public Optional<String> getHashValueByKey(String hashName, String key) {
        if (!jedis.exists(hashName)) {
            return Optional.empty();
        }
        String value = jedis.hget(hashName, key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public void setHash(String hashName, String key, String value) {
        jedis.hset(hashName, key, value);
    }


}

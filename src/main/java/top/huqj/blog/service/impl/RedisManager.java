package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
        jedis.rpush(listKey, value);
    }

    /**
     * 在列表首部添加一个元素
     *
     * @param listKey
     * @param value
     */
    public void addListOnHead(String listKey, String value) {
        jedis.lpush(listKey, value);
    }

    /**
     * 移除列表尾部一个元素
     *
     * @param listKey
     */
    public void removeListTail(String listKey) {
        jedis.rpop(listKey);
    }

    /**
     * 获取一个redis列表的所有值
     *
     * @param key
     * @return
     */
    public List<String> getListValues(String key) {
        return jedis.lrange(key, 0, -1);
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

    /**
     * 删除一个列表所有元素，直接删除该列表即可
     *
     * @param key
     */
    public void deleteListAllValue(String key) {
        jedis.del(key);
    }

    /**
     * 向一个列表中顺序添加多个元素
     *
     * @param listKey
     * @param values
     */
    public void addListValueBatch(String listKey, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        jedis.rpush(listKey, values.toArray(new String[values.size()]));
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

    public void removeHashKey(String hashName, String key) {
        jedis.hdel(hashName, key);
    }

    public void setWithExpireTime(String keyName, String value, int expire) {
        jedis.set(keyName, value);
        jedis.expire(keyName, expire);
    }

    public String getString(String key) {
        return jedis.get(key);
    }

}

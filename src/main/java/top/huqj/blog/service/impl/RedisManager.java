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
            jedis = new Jedis(redisHost, port, 1800);
        } catch (Exception e) {
            log.error("error when connect to redis", e);
            //fast fail
            log.error("Application exited!");
            System.exit(0);
        }
    }

    public Set<String> listKeysByPrefix(String prefix) {
        try {
            if (prefix == null) {
                return Collections.emptySet();
            }
            return jedis.keys(prefix + "*");
        } catch (Exception e) {
            log.error("jedis error", e);
            init();
        }
        return null;
    }

    public void set(String key, String value) {
        try {
            set(key, value, -1);
        } catch (Exception e) {
            log.error("jedis error ", e);
            init();
        }
    }

    public void set(String key, String value, int expire) {
        try {
            jedis.set(key, value);
            if (expire > 0) {
                jedis.expire(key, expire);
            }
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    /**
     * 向列表末尾中添加元素
     *
     * @param listKey
     * @param value
     */
    public void addList(String listKey, String value) {
        try {
            jedis.rpush(listKey, value);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    /**
     * 在列表首部添加一个元素
     *
     * @param listKey
     * @param value
     */
    public void addListOnHead(String listKey, String value) {
        try {
            jedis.lpush(listKey, value);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    /**
     * 移除列表尾部一个元素
     *
     * @param listKey
     */
    public void removeListTail(String listKey) {
        try {
            jedis.rpop(listKey);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    /**
     * 获取一个redis列表的所有值
     *
     * @param key
     * @return
     */
    public List<String> getListValues(String key) {
        try {
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
        return null;
    }

    /**
     * 获取列表中指定偏移量的值
     *
     * @param key
     * @param index
     * @return
     */
    public Optional<String> getByListIndex(String key, int index) {
        try {
            Long len = jedis.llen(key);
            if (index < 0 || index >= len) {
                return Optional.empty();
            }
            return Optional.of(jedis.lindex(key, index));
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
        return Optional.empty();
    }

    public long getListLength(String key) {
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
        return 0;
    }

    /**
     * 删除一个列表所有元素，直接删除该列表即可
     *
     * @param key
     */
    public void deleteListAllValue(String key) {
        try {
            jedis.del(key);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    /**
     * 向一个列表中顺序添加多个元素
     *
     * @param listKey
     * @param values
     */
    public void addListValueBatch(String listKey, List<String> values) {
        try {
            if (CollectionUtils.isEmpty(values)) {
                return;
            }
            jedis.rpush(listKey, values.toArray(new String[values.size()]));
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    public Optional<String> getHashValueByKey(String hashName, String key) {
        try {
            if (!jedis.exists(hashName)) {
                return Optional.empty();
            }
            String value = jedis.hget(hashName, key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(value);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
        return Optional.empty();
    }

    public void setHash(String hashName, String key, String value) {
        try {
            jedis.hset(hashName, key, value);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    public void removeHashKey(String hashName, String key) {
        try {
            jedis.hdel(hashName, key);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    public void setWithExpireTime(String keyName, String value, int expire) {
        try {
            jedis.set(keyName, value);
            jedis.expire(keyName, expire);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
    }

    public String getString(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            log.error("jedis error.", e);
            init();
        }
        return null;
    }

}

package seu.vczz.amall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * CREATE by vczz on 2018/4/9
 * token的本地缓存，用来设置用户重置密码的有效期
 */
public class TokenCache {
    //日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //token前缀
    public static final String TOKEN_PREFIX = "token_";

    //本地缓存块,使用LRU算法
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get方法，key没有对应的值时，就调用该方法进行加载
                @Override
                public String load(String key) throws Exception {
                    return "null";
                }
            });

    /**
     * setKey方法，设置key-value
     * @param key
     * @param value
     */
    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    /**
     * getKey方法，通过key获取value，如果key不存在，返回null
     * @param key
     * @return
     */
    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get error", e);
        }
        return null;
    }


}

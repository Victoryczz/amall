package seu.vczz.amall.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import seu.vczz.amall.common.RedisPool;

/**
 * CREATE by vczz on 2018/4/16
 * 针对RedisPool的封装API，只针对需要使用的功能进行封装，其实没有封装的需要；
 * 当然封装了更好
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * redis的set方法，使用带有设置有效期的方法
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value){
        //先是两个
        Jedis jedis = null;
        String result = null;
        //可能会报错，需要使用try catch
        try {
            jedis = RedisPool.getResource();
            result = jedis.set(key, value);
        } catch (Exception e) {
            //打印错误信息
            log.error("setex key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        //正常处理
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * redis的set方法，使用带有设置有效期的方法
     * @param key
     * @param value
     * @param exTime 秒
     * @return
     */
    public static String setEx(String key, String value, int exTime){
        //先是两个
        Jedis jedis = null;
        String result = null;
        //可能会报错，需要使用try catch
        try {
            jedis = RedisPool.getResource();
            result = jedis.setex(key, exTime, value);
            log.info("setex key:{} value:{} extime:{}", key, value, exTime);
        } catch (Exception e) {
            //打印错误信息
            log.error("setex key:{} value:{} extime:{} error", key, value, exTime, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        //正常处理
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 重新设置key的有效期
     * @param key
     * @param exTime 秒
     * @return
     */
    public static Long expire(String key, int exTime){
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.expire(key, exTime);
        }catch (Exception e){
            log.error("expire key:{} extime:{} error", key, exTime, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * redis的get方法
     * @param key
     * @return
     */
    public static String get(String key){
        //先是两个
        Jedis jedis = null;
        String result = null;
        //可能会报错，需要使用try catch
        try {
            jedis = RedisPool.getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            //打印错误信息
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        //正常处理
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * redis删除键操作
     * @param key
     * @return
     */
    public static Long del(String key){
        //先是两个
        Jedis jedis = null;
        Long result = null;
        //可能会报错，需要使用try catch
        try {
            jedis = RedisPool.getResource();
            result = jedis.del(key);
        } catch (Exception e) {
            //打印错误信息
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        //正常处理
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {

        RedisPoolUtil.set("keytest", "valuetest");
        String value = RedisPoolUtil.get("keytest");

        RedisPoolUtil.setEx("keyex", "valueex", 60*5);
        String value2 = RedisPoolUtil.get("keyex");
        //设置为1分钟
        RedisPoolUtil.expire("keyex", 60);

        RedisPoolUtil.del("keytest");

    }

}

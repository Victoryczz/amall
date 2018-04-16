package seu.vczz.amall.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import seu.vczz.amall.util.PropertiesUtil;

/**
 * CREATE by vczz on 2018/4/16
 * redis连接池,是对jedis连接池的封装
 */
public class RedisPool {
    //redis连接池
    private static JedisPool jedisPool;
    //redis连接池最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    //redis连接池中最大空闲线程的数量
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    //redis连接池中最小空闲线程的数量
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));
    //当从redis pool中请求线程时是否需要测试连接是否可用，默认测试，保证取得的都是可用连接
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    //当将连接归还至redis pool时是否需要测试连接是否可用，默认测试，保证归还的都是可用连接
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));
    //ip
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    //port
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port", "6379"));


    //初始化pool函数，供静态块调用
    private static void initRedisPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        //当连接耗尽时是否阻塞，默认是true，我们再设置一下
        poolConfig.setBlockWhenExhausted(true);
        //超时时间2秒，1000*2易读性高
        jedisPool = new JedisPool(poolConfig, redisIp, redisPort, 1000*2);
    }

    static {
        initRedisPool();
    }

    /**
     * 获得一个Jedis
     * @return
     */
    public static Jedis getResource(){
        return jedisPool.getResource();
    }

    /**
     * 放回连接池
     * @param jedis
     */
    public static void returnResource(Jedis jedis){
            jedisPool.returnResource(jedis);
    }

    /**
     * 将broken的resource放回连接池，源码中是减少数量并销毁当前实例
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }

    //测试
    public static void main(String[] args) {
        Jedis jedis = RedisPool.getResource();
        jedis.set("vczz", "victory********");
        returnResource(jedis);

        jedisPool.destroy();
        System.out.println("jedisPool destoried");
    }


}

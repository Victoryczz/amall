package seu.vczz.amall.common;

import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;
import seu.vczz.amall.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATE by vczz on 2018/4/18
 * redis分片连接池，采用一致性hash算法
 */
public class RedisShardedPool {


    //redis连接池,设置为shardedJedisPool
    private static ShardedJedisPool shardedJedisPool;
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
    private static String redisIp_1 = PropertiesUtil.getProperty("redis1.ip");
    //port
    private static Integer redisPort_1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port", "6379"));
    //ip
    private static String redisIp_2 = PropertiesUtil.getProperty("redis2.ip");
    //port
    private static Integer redisPort_2 = Integer.parseInt(PropertiesUtil.getProperty("redis2.port", "6380"));


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
        JedisShardInfo info1 = new JedisShardInfo(redisIp_1, redisPort_1, 1000*2);
        JedisShardInfo info2 = new JedisShardInfo(redisIp_2, redisPort_2, 1000*2);
        //构造list
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        //构造池
        shardedJedisPool = new ShardedJedisPool(poolConfig, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);


    }

    static {
        initRedisPool();
    }

    /**
     * 获得一个Jedis
     * @return
     */
    public static ShardedJedis getResource(){
        return shardedJedisPool.getResource();
    }

    /**
     * 放回连接池
     * @param jedis
     */
    public static void returnResource(ShardedJedis jedis){
        shardedJedisPool.returnResource(jedis);
    }

    /**
     * 将broken的resource放回连接池，源码中是减少数量并销毁当前实例
     * @param jedis
     */
    public static void returnBrokenResource(ShardedJedis jedis){
        shardedJedisPool.returnBrokenResource(jedis);
    }

    //测试
    public static void main(String[] args) {
        ShardedJedis jedis = shardedJedisPool.getResource();
        for (int i = 0; i < 10; i++){
            jedis.set("key"+i, "value"+1);
        }


        returnResource(jedis);

        //shardedJedisPool.destroy();
        System.out.println("shardedjedisPool destoried");
    }


}

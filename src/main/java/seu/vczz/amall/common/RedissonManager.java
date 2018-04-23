package seu.vczz.amall.common;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;
import seu.vczz.amall.util.PropertiesUtil;
import javax.annotation.PostConstruct;

/**
 * CREATE by vczz on 2018/4/23
 * Redisson初始化类
 */
@Component
@Slf4j
public class RedissonManager {
    //配置
    private Config config = new Config();
    //redisson实例
    private Redisson redisson = null;
    //ip
    private static String redisIp_1 = PropertiesUtil.getProperty("redis1.ip");
    //port
    private static Integer redisPort_1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port", "6379"));
    //ip
    private static String redisIp_2 = PropertiesUtil.getProperty("redis2.ip");
    //port
    private static Integer redisPort_2 = Integer.parseInt(PropertiesUtil.getProperty("redis2.port", "6380"));
    //在执行完构造函数之后执行
    @PostConstruct
    private void init(){
        try {
            //配置
            config.useSingleServer().setAddress(redisIp_1+":"+redisPort_1);
            //create Redisson实例
            redisson = (Redisson) Redisson.create(config);
            log.info("初始化Redisson实例结束");
        } catch (Exception e) {
            log.error("初始化Redisson实例失败：", e);
        }
    }

    public Redisson getRedisson() {
        return redisson;
    }
}

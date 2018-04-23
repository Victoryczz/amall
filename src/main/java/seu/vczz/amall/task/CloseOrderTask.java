package seu.vczz.amall.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.service.IOrderService;
import seu.vczz.amall.util.PropertiesUtil;
import seu.vczz.amall.util.RedisShardedPoolUtil;

import java.util.concurrent.TimeUnit;

/**
 * CREATE by vczz on 2018/4/21
 * 定时任务task类
 */
@Component
@Slf4j
public class CloseOrderTask {
    //定时关单
    @Autowired
    private IOrderService iOrderService;
    /**
     * 定时关单v1版本，该版本不需要分布式锁，也就是
     * 如果集群中多个任务同时关单就可能发生数据错误了，
     * 同时有另外一个问题就是只需要一个应用执行关单就行了，多了不用
     */
    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次该任务
    public void closeOrderTaskV1(){
        log.info("关闭订单任务启动");
        //从配置文件读取，默认2小时
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //每一分钟执行一次任务，该任务会关闭两个小时未付款的订单
        iOrderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }
    /**
     * 定时关单v2版本,逻辑是setNx获取锁，如果获取锁成功，则设置锁的时间，调用关闭订单的方法，之后自动释放，其他线程没有判断value的值
     * 所以该版本可能会造成死锁：获取锁之后就挂掉，没有成功设置锁的有效期
     */
    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次该任务
    public void closeOrderTaskV2(){
        log.info("关闭订单任务启动");
        //分布式锁的超时时间
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "5000"));
        //设置分布式锁,注意，setNx是原子操作，而且如果setNx成功了，需要设置锁的有效期，避免进程挂掉的时候锁一直占用（当前版本是未优化版本）
        Long setNxResult = RedisShardedPoolUtil.setNx(Const.RedisLock.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeOut));
        if (setNxResult != null && setNxResult.intValue() == 1){
            //如果返回1则设置成功，也就是获得了锁
            //接下来需要设置锁的时间，其实这里是会出问题的，如果在设置锁的时间之前挂掉了呢，锁就一直占用了（当前版本是未优化版本）
            this.closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }else {
            log.info("没有获得分布式锁：{}", Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单任务结束");
    }
    private void closeOrder(String lockName){
        //设置锁的有效期，暂时为50s方便调试
        RedisShardedPoolUtil.expire(lockName, 5);
        log.info("获取{}，ThreadName：{}", Const.RedisLock.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //调用关闭订单
        iOrderService.closeOrder(hour);
        //订单关闭完成后释放锁
        RedisShardedPoolUtil.del(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        log.info("释放：{}，ThreadName：{}", Const.RedisLock.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
        log.info("-----------------------------------");
    }
    /**
     * 分布式锁v3版本，v2的优化版本
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void closeOrderTaskV3(){
        log.info("关闭订单任务启动");
        //分布式锁的超时时间
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "5000"));
        //设置分布式锁,注意，setNx是原子操作
        Long setNxResult = RedisShardedPoolUtil.setNx(Const.RedisLock.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeOut));
        if (setNxResult != null && setNxResult.intValue() == 1){
            //如果返回1则设置成功，也就是获得了锁
            this.closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }else {
            //相较于v2的优化之处，如果没有获得分布式锁，那么就应该判断锁的时间是否超时，如果超时是可以重新获得锁的
            log.info("没有获得分布式锁：{}，尝试判断超时时间", Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            //判断时间戳
            String lockValue = RedisShardedPoolUtil.get(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            //如果当前时间超出了锁的超时时间
            if (lockValue != null && System.currentTimeMillis() > Long.parseLong(lockValue)){
                //getSet新的锁时间
                String getSetValue = RedisShardedPoolUtil.getSet(Const.RedisLock.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeOut));
                //如果返回的旧值为空或者是返回的旧值没有变化
                if (getSetValue == null || (getSetValue != null && StringUtils.equals(lockValue, getSetValue))){
                    //进行业务操作
                    this.closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
                }else {
                    log.info("没有获得分布式锁：{}", Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
                }
            }else {
                log.info("没有获得分布式锁：{}", Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            }


        }
        log.info("关闭订单任务结束");
    }
}

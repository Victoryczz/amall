package seu.vczz.amall.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import seu.vczz.amall.service.IOrderService;
import seu.vczz.amall.util.PropertiesUtil;

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
    @Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次该任务
    public void closeOrderTaskV1(){
        log.info("关闭订单任务启动");
        //从配置文件读取，默认2小时
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //每一分钟执行一次任务，该任务会关闭两个小时未付款的订单
        iOrderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }
}

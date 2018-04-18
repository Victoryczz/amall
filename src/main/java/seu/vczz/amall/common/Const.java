package seu.vczz.amall.common;

import com.google.common.collect.Sets;
import java.util.Set;

/**
 * CREATE by vczz on 2018/4/9
 * 常用常量类、接口以及枚举
 */
public class Const {
    //当前用户
    public static final String CURRENT_USER = "current_user";
    //实时校验时的type
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String TOKEN_PREFIX = "token_";

    //查询产品列表排序传入的字符串，升序、降序
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
    //购物车条目的状态、购物车商品数量限制状态
    public interface Cart{
        int CHECKED = 1;//选中状态
        int UN_CHECKED = 0;//非选中状态
        String LIMIT_NUM_FAILED = "限制失败";
        String LIMIT_NUM_SUCCESS = "限制成功";
    }

    //用户权限
    public interface Role{
        //普通用户
        int ROLE_CUSTOMER = 0;
        //管理员
        int ROLE_ADMIN = 1;
    }
    //商品状态,1表示在售
    public enum ProductStatusEnum{

        ON_SALE(1,"在售");

        private String value;
        private int code;

        ProductStatusEnum(int code, String value){
            this.code = code;
            this.value = value;

        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
    //订单状态
    public enum OrderStatusEnum{

        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSED(60, "订单关闭");

        private int code;
        private String value;

        OrderStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
        //要通过一个数字的code能够返回这个枚举，然后获得描述
        public static OrderStatusEnum codeOf(int code){
            //这个values()方法很神奇
            for (OrderStatusEnum orderStatusEnum : values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("找不到枚举");
        }
    }
    //只使用常量的话就用interface，上面每个有两个值用了enum
    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
    //交易平台
    public enum PayPlatformEnum{

        ALIPAY(1, "支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

    }
    //支付方式枚举
    public enum PaymentTypeEnum{

        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;

        PaymentTypeEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static PaymentTypeEnum codeOf(int code){
            //枚举的奇怪的用法
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw  new RuntimeException("没有找到相应的枚举");
        }
    }
    //redis缓存时间
    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60*30;//30min

    }



}

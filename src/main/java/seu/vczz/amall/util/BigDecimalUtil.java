package seu.vczz.amall.util;

import java.math.BigDecimal;

/**
 * CREATE by vczz on 2018/3/20
 * BigDecimal工具类，在使用浮点型或double进行商业运算时候，会出现误差，bigdecimal也有同样的问题，
 * 因此一定要使用bigdecimal的string构造器，可以先将double转为string再用bigdecimal
 */
public class BigDecimalUtil {

    private BigDecimalUtil(){

    }

    /**
     * 加法
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    public static BigDecimal multi(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    public static BigDecimal div(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);//保留两位小数，四舍五入
    }

}

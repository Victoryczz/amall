package seu.vczz.amall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * CREATE by vczz on 2018/4/10
 * 这是未提交订单，但是选中了结算后的Vo,此时展示给用户将要购买哪些产品
 */
public class OrderProductVo {

    private List<OrderItemVo> orderItemVoList;//orderItem的vo
    private BigDecimal productTotalPrice;//商品总价
    private String imageHost;//图片地址

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

package seu.vczz.amall.vo;

import java.math.BigDecimal;

/**
 * CREATE by vczz on 2018/4/10
 * 订单item的明细，在订单vo中以集合的方式存在
 */
public class OrderItemVo {

    private Long orderNo;//订单号
    private Integer productId;//产品id
    private String productName;//产品名称
    private String productImage;//产品主图
    private BigDecimal currentUnitPrice;//当前产品单价，也就是付款时的单价
    private Integer quantity;//产品数量
    private BigDecimal totalPrice;//产品付款总价
    private String createTime;//item创建时间

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

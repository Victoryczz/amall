package seu.vczz.amall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * CREATE by vczz on 2018/4/10
 * 购物车Vo，包含CartProductVo的集合
 */
public class CartVo {


    private List<CartProductVo> cartProductVoList;

    private BigDecimal cartTotalPrice;
    private boolean allChecked;//是否全部勾选
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

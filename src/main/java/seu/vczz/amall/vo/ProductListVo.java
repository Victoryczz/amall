package seu.vczz.amall.vo;

import java.math.BigDecimal;

/**
 * CREATE by vczz on 2018/3/18
 * 产品列表详情Vo
 */
public class ProductListVo {
    //相较于产品详情vo，产品列表vo只需要以下几个即可
    private Integer id;//id
    private Integer categoryId;//分类id
    private String name;//名称
    private String subtitle;//副标题
    private String mainImage;//主图

    private BigDecimal price;//使用bigdecimal
    private Integer status;//状态

    private String imageHost;//图片主机

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

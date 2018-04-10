package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.vo.ProductDetailVo;

/**
 * CREATE by vczz on 2018/4/9
 * 产品服务接口
 */
public interface IProductService {
    /**
     * 后端管理员保存或更新产品
     * @param product
     * @return
     */
    ServerResponse saveOrUpdateProduct(Product product);

    /**
     * 产品上下架
     * @param productId
     * @param status
     * @return
     */
    ServerResponse setSaleStatus(Integer productId, Integer status);

    /**
     * 管理商品详情
     * @param productId
     * @return
     */
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    /**
     * 获取产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse getProductList(int pageNum, int pageSize);

    /**
     * 查找商品
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    /**
     * 前台获取商品详情
     * @param productId
     * @return
     */
    ServerResponse getProductDetail(Integer productId);

    /**
     * 前台查询商品
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    ServerResponse list(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}

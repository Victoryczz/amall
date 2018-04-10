package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.vo.ProductDetailVo;

/**
 * CREATE by vczz on 2018/4/9
 * 产品服务接口
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum, int pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse list(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}

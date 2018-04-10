package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;

/**
 * CREATE by vczz on 2018/4/10
 * 购物车服务接口
 */
public interface ICartService {
    /**
     * 添加购物车
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    ServerResponse add(Integer userId, Integer count, Integer productId);

    /**
     * 更新购物车中商品数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    ServerResponse update(Integer userId, Integer productId, Integer count);

    /**
     * 批量删除购物车中商品
     * @param userId
     * @param productIds
     * @return
     */
    ServerResponse deleteProduct(Integer userId, String productIds);

    /**
     * 查询购物车，其实是当点击查看购物车时展示
     * @param userId
     * @return
     */
    ServerResponse list(Integer userId);

    /**
     * 选中或反选购物车中商品
     * @param userId
     * @param checked
     * @param productId
     * @return
     */
    ServerResponse selectOrUnSelect(Integer userId, Integer checked, Integer productId);

    /**
     * 获得购物车中产品的数量
     * @param userId
     * @return
     */
    ServerResponse getCartProductCount(Integer userId);

}

package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Shipping;

/**
 * CREATE by vczz on 2018/4/10
 * 收货地址服务接口
 */
public interface IShippingService {
    /**
     * 添加地址
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * 删除地址
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse delete(Integer userId, Integer shippingId);

    /**
     * 更新地址
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse update(Integer userId, Shipping shipping);

    /**
     * 选择地址
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse select(Integer userId, Integer shippingId);

    /**
     * 显示地址列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse list(Integer userId, int pageNum, int pageSize);

}

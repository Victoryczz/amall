package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;

import java.util.Map;

/**
 * CREATE by vczz on 2018/4/10
 * 订单服务接口
 */
public interface IOrderService {
    /**
     * 付款，入参订单号，用户id，路径
     * @param userId
     * @param orderNo
     * @param path
     * @return
     */
    ServerResponse pay(Integer userId, Long orderNo, String path);

    /**
     * 支付宝回调接口，当用户支付完成，支付宝会调用该接口
     * @param params
     * @return
     */
    ServerResponse aliCallback(Map<String, String> params);

    /**
     * 查询订单状态，除了支付宝回调，前台也可以一直查询订单状态
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse cancelOrder(Integer userId, Long orderNo);

    /**
     * 获取订单中产品
     * @param userId
     * @return
     */
    ServerResponse getOrderCartProduct(Integer userId);

    /**
     * 获取订单详情
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse getOrderDetail(Integer userId, Long orderNo);

    /**
     * 用户查询订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse getOrderList(Integer userId, int pageNum, int pageSize);

    /**
     * 后台查询订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse getManageOrderList(int pageNum, int pageSize);

    /**
     * 后台管理订单详情
     * @param orderNo
     * @return
     */
    ServerResponse getManageOrderDetail(Long orderNo);

    /**
     * 查询订单，使用的是订单号的详细匹配
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse searchOrder(Long orderNo, int pageNum, int pageSize);

    /**
     * 发货管理
     * @param orderNo
     * @return
     */
    ServerResponse sendGoods(Long orderNo);
}

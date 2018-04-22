package seu.vczz.amall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jmx.remote.util.OrderClassLoaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.dao.*;
import seu.vczz.amall.pojo.*;
import seu.vczz.amall.service.IOrderService;
import seu.vczz.amall.util.BigDecimalUtil;
import seu.vczz.amall.util.DateTimeUtil;
import seu.vczz.amall.util.FTPUtil;
import seu.vczz.amall.util.PropertiesUtil;
import seu.vczz.amall.vo.OrderItemVo;
import seu.vczz.amall.vo.OrderProductVo;
import seu.vczz.amall.vo.OrderVo;
import seu.vczz.amall.vo.ShippingVo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * CREATE by vczz on 2018/4/10
 */
@Service("iOrderService")
@Slf4j
public class OrderServiceImpl implements IOrderService {
    //日志
    //private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    //当面付。从支付宝demo中拿过来
    private static AlipayTradeService tradeService;
    //从支付宝demo中拿过来
    static {
        //将demo中的Main中的声明拿过来
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");
        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }
    ///////end 支付宝

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse createOrder(Integer userId, Integer shippingId){
        //根据userId获得购物车中选中并提交的cart
        List<Cart> cartList = cartMapper.selectCheckCartByUserId(userId);
        //接下来就是计算订单的总价，插入订单以及订单的条目
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        //如果不成功，直接将response返回
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);
        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        //判断订单有没有生成成功
        if (order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        //判断orderItemList是否为空
        if (orderItemList == null){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);
        //插入成功后，要减少一下产品库存
        this.reduceProductStocks(orderItemList);
        //清空一下购物车
        this.cleanCart(cartList);
        //返回给前端一个orderVo视图
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }
    //装配orderVo
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping == null){
            return null;
        }
        orderVo.setReceiverName(shipping.getReceiverName());
        orderVo.setShippingVo(this.assembleShippingVo(shipping));

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList){
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }
    //装配orderItemVo
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    //装配shippingVo
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }
    //根据orderItem的数量减少商品的库存数量
    private void reduceProductStocks(List<OrderItem> orderItemList){
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    //清空购物车(其实是删除已经提交订单的购物车)
    private void cleanCart(List<Cart> cartList){
        for (Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    //装配订单
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment){
        long orderNo = this.generateOrderNo();
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);//运费
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        //发货时间啥的先不管,包括付款时间，因为现在只是创建订单
        //插入
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0){
            return order;
        }
        return null;
    }
    //生成订单号，先使用较为简单的方式
    //订单号的规划很重要，可能需要通过订单号中的某个数字来获取某些表，之后再进行改进
    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
    //计算订单的总价
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }
    //将购物车中的cart装换成orderItem
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList){
        //初始化
        List<OrderItem> orderItemList = Lists.newArrayList();
        //如果购物车时空的
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //校验购物车的数据，包括产品的状态和数量，然后创建OrderItem
        for (Cart cart : cartList){
            OrderItem orderItem = new OrderItem();
            //从购物车中获取产品
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //如果产品不是在售状态
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                return ServerResponse.createByErrorMessage("产品不是在售状态");
            }
            //判断产品数量
            if (cart.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }
            //否则添加OrderItem
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());//当前的价格，避免之后改了价格
            orderItem.setTotalPrice(BigDecimalUtil.multi(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * 取消订单,这里没有增加库存，应该做的，之后用定时取消订单来做
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse cancelOrder(Integer userId, Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        //如果用户订单不存在
        if (order == null){
            return ServerResponse.createByErrorMessage("改用户没有该订单");
        }
        //判断订单是不是应付过款了，付过款就不能取消了
        if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMessage("订单已付款，不能取消");
        }
        //使用一个新的订单来更新比较好，因为原始查出来的订单东西太多了
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("取消订单成功");
        }
        return ServerResponse.createByErrorMessage("取消订单失败");
    }

    /**
     * 应该是提交订单前的一页，展示你要买的哪些东西，但是此时只是从购物车转到了提交页面，真正的订单还没有创建，相当于淘宝点了结算还没提交订单
     * @param userId
     * @return
     */
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //同创建订单一样，从购物车中获取数据
        //购物车中选中的
        List<Cart> cartList = cartMapper.selectCheckCartByUserId(userId);
        //获得orderItem
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        //获得orderItemVo
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        for (OrderItem orderItem : orderItemList){
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        //装配orderProductVo
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    /**
     * 获取订单详情
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse getOrderDetail(Integer userId, Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("未找到该订单");
        }
        //根据订单号和用户id获得orderItem
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, orderNo);
        //组装orderVo
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * 获得订单列表，需要分页
     * @param userId
     * @return
     */
    public ServerResponse getOrderList(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectOrderByUserId(userId);
        //需要装配orderVoList了
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, userId);
        //分页
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    //装配orderVoList，提供userId为了前台查询，同时后台查询传null
    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        //对于每一个order，都装饰成一个vo
        for (Order order : orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();
            //如果传过来的userId是null，就是后台管理员
            if (userId == null){
                //后台
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            }else {//否则就是个普通的用户查询
                orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, order.getOrderNo());
            }
            orderVoList.add(this.assembleOrderVo(order, orderItemList));
        }
        return orderVoList;
    }

    ///////////////////////////backend/////////////////
    /**
     * 后台获得订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getManageOrderList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        //orderVoList
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 后台获取订单详情
     * @param orderNo
     * @return
     */
    public ServerResponse getManageOrderDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);

    }

    /**
     * 订单查找,当前是精确匹配，后期加入模糊查询;当前先加上分页
     * @param orderNo
     * @return
     */
    public ServerResponse searchOrder(Long orderNo, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在呀");
        }
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 订单发货
     * @param orderNo
     * @return
     */
    public ServerResponse sendGoods(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            updateOrder.setSendTime(new Date());
            updateOrder.setUpdateTime(new Date());
            int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("发货成功");
            }
            return ServerResponse.createBySuccessMessage("更新发货状态失败");
        }
        return ServerResponse.createBySuccessMessage("订单未付款");

    }

    /**
     * 定时关单，超过hour小时未付款的订单取消
     * @param hour
     */
    public void closeOrder(int hour) {
        //关单的时间,当前时间减去hour
        Date closeDateTime = DateUtils.addHours(new Date(), -hour);
        //然后需要根据时间查找符合条件的订单列表
        List<Order> orderList = orderMapper.selectOrderStatusByCreateTime(Const.OrderStatusEnum.NO_PAY.getCode(),
                                                                          DateTimeUtil.dateToStr(closeDateTime));
        //遍历order，拿到orderItem，增加库存
        for (Order order : orderList){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            for (OrderItem orderItem : orderItemList){
                //获得产品的库存
                Integer stock = productMapper.selectStockByProductId(orderItem.getProductId());
                if (stock == null){
                    //代表订单中的产品已经被删除了
                    continue;
                }
                //重新写入数据库
                Product product = new Product();
                product.setId(orderItem.getProductId());
                product.setStock(stock+orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
            //当前order取消之后，要关闭订单,设置订单状态为已取消
            orderMapper.closeOrderByOrderId(order.getId());
            log.info("关闭订单：{}", order.getOrderNo());
        }
    }


    /********************************一下都是支付宝对接的服务*****************************/
    /**
     * 根据用户id和订单号预下单，向支付宝提交请求，支付宝返回二维码，将二维码放在ftp服务器供前端调用
     * @param userId
     * @param orderNo
     * @param path
     * @return
     */
    public ServerResponse pay(Integer userId, Long orderNo, String path){
        Map<String, String> resultMap = Maps.newHashMap();
        //先通过userId和订单号同时判断订单存不存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        //将订单号放进来
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        ////////////////////alipay_demo///////////
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("amall扫码支付,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        //将我们自己的商品放入list
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, orderNo);
        for (OrderItem orderItem : orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.multi(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);
        //vczz 具体处理请求的位置在这里应该,拿到预下单的处理结果
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);//简单打印响应

                //以下为处理二维码流程
                //查看上传二维码的路径是否存在，不存在在创建路径
                File folder = new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径,保留原来的
                //二维码路径及二维码名称
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);//获取二维码，google的二维码制作工具，支付宝提供的封装
                //同时将图片放到ftp
                File targetFile = new File(path, qrFileName);
                try {
                    //上传至ftp
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传二维码异常",e);
                    e.printStackTrace();
                }
                log.info("qrPath:" + qrPath);
                //需要返回给前端的二维码路径
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                //再将qrUrl放到map中
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    /**
     * 支付宝回调的处理,传入请求参数，然后我们更新数据库订单状态以及支付状态等信息，默认只有支付成功才出发回调，这里是回调后数据库更新的处理
     * @param params
     * @return
     */
    public ServerResponse aliCallback(Map<String, String> params){
        //回调的流程需要去看支付宝官方文档,大概是需要先处理请求参数，因为传过来value用的是string[]，然后移除两个key-value，然后进行验签
        Long orderNo = Long.parseLong(params.get("out_trade_no"));//交易订单号
        String tradeNo = params.get("trade_no");//支付宝交易号
        String tradeStatus = params.get("trade_status");
        log.info("支付宝交易状态："+tradeStatus);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("非mmall商城的订单,忽略回调");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            //处理重复回调的问题
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        //注意，支付默认的支付成功才会触发回调，所以如果其他状态不会回调，所以这没有处理
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //如果支付宝回调是交易成功，那么就将订单交易状态改为成功
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //更新回数据库
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        //更新交易状态表
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    /**
     * 查询订单的交易状态，默认的都是未支付10
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        //如果是大于已付款直接返回
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}

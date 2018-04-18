package seu.vczz.amall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IOrderService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisShardedPoolUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * CREATE by vczz on 2018/4/10
 * 前台订单模块
 */
@Controller
@RequestMapping(value = "/order/")
public class OrderController {

    //日志记录
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单，当提交订单时，会选择地址，此时将地址id传过来就可以获取其他信息
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest request, Integer shippingId){
        //用户校验
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能创建订单");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单，只需要提价订单号即可
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest request, Long orderNo){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能取消订单");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    /**
     * 应该是提交订单前的一页，展示你要买的哪些东西，但是此时只是从购物车转到了提交页面，真正的订单还没有创建，相当于淘宝点了结算还没提交订单
     * @param request
     * @return
     */
    @RequestMapping("get_cart_order_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取购物车商品");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 获取订单详情，只需要传入订单号即可
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping("get_order_detail.do")
    @ResponseBody
    public ServerResponse getOrderDetail(HttpServletRequest request, Long orderNo){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取订单详情");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 获取订单列表,需要加上分页的逻辑
     * @param request
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查询订单列表");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    /**************************************以下都是支付宝的对接************************************/
    /**
     * 支付，提交后需要向支付宝预下单，支付宝返回二维码，这里再返回给前端展示，之后用户扫码扫码付款
     * @param request 判断用户登录状态
     * @param orderNo 订单号
     * @param request 获得路径
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        //检查用户状态
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能支付");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //获得上传文件的路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), orderNo, path);
    }

    /**
     * 支付宝的回调函数，当用户支付成功后，支付宝会调用该函数，也就是我们传过去的url
     * @param request 支付宝将所有的参数都放在请求里
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        //我们自己的map去接请求的参数
        Map<String, String> params = Maps.newHashMap();

        //请求参数的map，该函数的返回的value是string[]的类型，所以需要我们自己去接一下
        Map requestParams = request.getParameterMap();
        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext();){
            String name = (String) iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String value = "";
            for (int i = 0; i < values.length; i++){
                value = (i == values.length-1)?value+values[i]:value+values[i]+",";
            }
            params.put(name, value);
        }
        LOGGER.info("支付宝回调,sign{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //下一步非常重要，需要验证回调是不是支付宝发出的，并且避免重复通知,验证签名，只有签名啥的是加密的
        //sdk中有移除另外一个参数，所以这里只将该参数移除
        params.remove("sign_type");//看文档
        try {
            //这里开始是用的getPublicKey，发现错了
            //验证签名
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckV2){
                return ServerResponse.createByErrorMessage("非法请求");
            }

        } catch (AlipayApiException e) {
            LOGGER.error("支付宝验证异常", e);
            e.printStackTrace();
        }
        //todo 验证各种数据

        //业务逻辑
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        //成功了就向支付宝回复success,必须是“success”字符串
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 通过userId和orderNo查询订单的状态
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request, Long orderNo){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能请求订单支付状态");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);

    }



}

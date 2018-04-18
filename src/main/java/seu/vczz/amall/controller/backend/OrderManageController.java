package seu.vczz.amall.controller.backend;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IOrderService;
import seu.vczz.amall.service.IUserService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisPoolUtil;
import javax.servlet.http.HttpServletRequest;


/**
 * CREATE by vczz on 2018/4/10
 * 后台订单管理
 */
@Controller
@RequestMapping(value = "/manage/order/")
public class OrderManageController {


    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;

    /**
     * 获得订单列表
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse orderList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取订单列表");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("该用户没有管理员权限");
        }
        return iOrderService.getManageOrderList(pageNum, pageSize);

    }

    /**
     * 后台获取订单详情
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpServletRequest request, Long orderNo){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取订单详情");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("该用户没有管理员权限");
        }
        return iOrderService.getManageOrderDetail(orderNo);
    }

    /**
     * 查找订单，当前只用了精确查找，且分页
     * @param request
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpServletRequest request, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能搜寻订单");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("该用户没有管理员权限");
        }
        return iOrderService.searchOrder(orderNo, pageNum, pageSize);
    }

    /**
     * 后台发货
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "sendGoods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpServletRequest request, Long orderNo){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能发货");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("该用户没有管理员权限");
        }
        return iOrderService.sendGoods(orderNo);
    }


}

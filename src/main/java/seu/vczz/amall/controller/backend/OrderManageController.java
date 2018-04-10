package seu.vczz.amall.controller.backend;

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
import seu.vczz.amall.service.IUserService;

import javax.servlet.http.HttpSession;

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
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpSession session, Long orderNo){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "sendGoods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session, Long orderNo){
        //查看是否具有管理员权限，之后会使用springMVC进行统一的权限拦截
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("该用户没有管理员权限");
        }
        return iOrderService.sendGoods(orderNo);
    }


}

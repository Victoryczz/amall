package seu.vczz.amall.controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Shipping;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IShippingService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisShardedPoolUtil;
import javax.servlet.http.HttpServletRequest;

/**
 * CREATE by vczz on 2018/4/10
 * 收货地址模块
 */
@Controller
@RequestMapping(value = "/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 添加收获地址
     * @param request
     * @param shipping
     * @return
     */
    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能添加收获地址");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(), shipping);
    }

    /**
     * 删除，这时候知道返回id的作用了，后端返回id
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest request, Integer shippingId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能删除收获地址");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.delete(user.getId(), shippingId);
    }

    /**
     * 更新收货地址
     * @param request
     * @param shipping
     * @return
     */
    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Shipping shipping){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能更新收获地址");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(), shipping);
    }

    /**
     * 查询地址,加上了shipping，难道是在修改之前先查询展示
     * @param request
     * @return
     */
    @RequestMapping(value = "get.do")
    @ResponseBody
    public ServerResponse get(HttpServletRequest request, Integer shippingId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查询收货地址");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(), shippingId);
    }

    /**
     * 查询地址信息列表
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查询地址列表");
        }
        //拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }

}

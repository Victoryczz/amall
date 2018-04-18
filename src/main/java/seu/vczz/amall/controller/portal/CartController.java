package seu.vczz.amall.controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.ICartService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisPoolUtil;
import javax.servlet.http.HttpServletRequest;


/**
 * CREATE by vczz on 2018/4/10
 * 购物车模块
 */
@Controller
@RequestMapping(value = "/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * 添加商品到购物车
     * @param request
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Integer count, Integer productId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能添加购物车");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }

        return iCartService.add(user.getId(), count, productId);
    }

    /**
     * 更新购物车中商品的数量
     * @param request
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Integer count, Integer productId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能更新购物车");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.update(user.getId(), productId, count);
    }

    /**
     * 删除购物车中商品，productId使用逗号分隔，可以同时删除多个商品
     * @param request
     * @param productIds
     * @return
     */
    @RequestMapping(value = "delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpServletRequest request, String productIds){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能删除购物车商品");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    /**
     * 查询购物车
     * @param request
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查询购物车");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.list(user.getId());
    }

    /**
     * 全选
     * @param request
     * @return
     */
    @RequestMapping(value = "selectAll.do")
    @ResponseBody
    public ServerResponse selectAll(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能选择");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED, null);
    }

    /**
     * 全不选
     * @param request
     * @return
     */
    @RequestMapping(value = "unSelectAll.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能选择");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, null);
    }

    /**
     * 单选
     * @param request
     * @return
     */
    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse select(HttpServletRequest request, Integer productId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能选择");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED, productId);
    }

    /**
     * 不选
     * @param request
     * @return
     */
    @RequestMapping(value = "unSelect.do")
    @ResponseBody
    public ServerResponse unSelect(HttpServletRequest request, Integer productId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能选择");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, productId);
    }

    /**
     * 获得购物车中产品的数量
     * @param request
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查询购物车数量");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());

    }

}

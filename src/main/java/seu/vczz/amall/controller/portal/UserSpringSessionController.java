package seu.vczz.amall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IUserService;
import javax.servlet.http.HttpSession;

/**
 * CREATE by vczz on 2018/4/19
 * 暂时保留之前的UserController，拷贝一份测试spring session
 */

@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录,限制POST登录,@ResponseBody通过MVC的配置文件返回json格式
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession){
        //-->service-->dao
        //获取服务返回
        ServerResponse<User> serverResponse =  iUserService.login(username, password);
        //如果用户存在,放进session
        if (serverResponse.isSuccess()){
            httpSession.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }
    /**
     * 用户登出，只需要移除session即可
     * @param
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession httpSession){
        //移除session的属性
        httpSession.removeAttribute(Const.CURRENT_USER);

        return ServerResponse.createBySuccess();
    }
    /**
     * 获取用户信息
     * @param session 请求
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //感觉重构麻烦了
        if (user != null)
            return ServerResponse.createBySuccess(user);
        return ServerResponse.createByErrorMessage("用户未登录，不能获取用户信息");
    }
}

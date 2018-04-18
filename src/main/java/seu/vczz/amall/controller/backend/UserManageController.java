package seu.vczz.amall.controller.backend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IUserService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisPoolUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CREATE by vczz on 2018/4/9
 * 后台管理员登录系统
 */
@Controller
@RequestMapping(value = "/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录,限制POST登录,@ResponseBody通过MVC的配置文件返回json格式
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletResponse response, HttpSession session){
        //-->service-->dao
        //获取服务返回
        ServerResponse<User> serverResponse =  iUserService.login(username, password);
        //如果用户存在,放进session
        if (serverResponse.isSuccess()){
            User user = serverResponse.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                //是管理员
                //httpSession.setAttribute(Const.CURRENT_USER, user);
                //登录成功则放进去
                //如果登录成功，直接将session放到redis中
                CookieUtil.writeLoginToken(response, session.getId());
                RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(user), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);


                return serverResponse;
            }else
                return serverResponse.createByErrorMessage("不是管理员，无法登录");
        }
        return serverResponse;
    }


}

package seu.vczz.amall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IUserService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisPoolUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CREATE by vczz on 2018/4/9
 * 前台用户登录接口,查看接口文档,所有的请求都在"/usr/"下
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录,限制POST登录,@ResponseBody通过MVC的配置文件返回json格式
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession, HttpServletResponse response){
        //-->service-->dao
        //获取服务返回
        ServerResponse<User> serverResponse =  iUserService.login(username, password);
        //如果用户存在,放进session
        if (serverResponse.isSuccess()){
            //httpSession.setAttribute(Const.CURRENT_USER, serverResponse.getData());  v1.0
            //如果登录成功，直接将session放到redis中
            CookieUtil.writeLoginToken(response, httpSession.getId());

            RedisPoolUtil.setEx(httpSession.getId(), JsonUtil.obj2String(serverResponse.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return serverResponse;
    }

    /**
     * 用户登出，只需要移除session即可
     * @param
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout(HttpServletRequest request, HttpServletResponse response){
        //移除session的属性
        //httpSession.removeAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        //直接先删除cookie
        CookieUtil.delLoginToken(request, response);
        //在删除redis
        RedisPoolUtil.del(loginToken);

        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 实时校验，应该是个异步提交，输入时候校验是否存在
     * 当最终提交的时候就是调用register方法
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null){
            return ServerResponse.createByErrorMessage("用户未登录，不能获取用户信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        //感觉重构麻烦了
        if (user != null)
            return ServerResponse.createBySuccess(user);
        return ServerResponse.createByErrorMessage("用户未登录，不能获取用户信息");
    }

    /**
     * 获取密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 验证问题答案，如果正确了，返回前端一个token，作为重改密码的有效期
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.forgetCheckAnswer(username, question, answer);
    }

    /**
     * 忘记密码之重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态重置密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorMessage("还未登录");
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user 上传的user
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("还未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = iUserService.updateInformation(user);
        //如果更新成功,更新当前用户
        if (serverResponse.isSuccess())
            //少了这一句
            serverResponse.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，请登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }














}

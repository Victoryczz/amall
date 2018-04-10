package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;

/**
 * CREATE by vczz on 2018/4/9
 * 用户服务接口
 */
public interface IUserService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 新用户注册
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 用户名和密码校验（用来注册时实时校验）
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * 获取密保问题
     * @param username
     * @return
     */
    ServerResponse<String> selectQuestion(String username);

    /**
     * 忘记密码之验证答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    /**
     * 忘记密码之重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 登录状态重置密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    ServerResponse<User> updateInformation(User user);

    /**
     * 获取个人信息
     * @param userId
     * @return
     */
    ServerResponse<User> getInformation(Integer userId);

    /**
     * 检查是否具有管理员权限
     * @param user
     * @return
     */
    ServerResponse checkAdminRole(User user);

}

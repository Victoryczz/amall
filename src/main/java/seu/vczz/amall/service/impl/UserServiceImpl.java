package seu.vczz.amall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.common.TokenCache;
import seu.vczz.amall.dao.UserMapper;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IUserService;
import seu.vczz.amall.util.MD5Util;

import java.util.UUID;

/**
 * CREATE by vczz on 2018/4/9
 * 用户服务接口的实现
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{
    //注入userMapper

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public ServerResponse<User> login(String username, String password) {
        //首先根据用户名检查用户名是否存在
        if (0 == userMapper.checkUsername(username))
            return ServerResponse.createByErrorMessage("用户名不存在");

        //如果用户名存在，将密码使用MD5加密然后对比
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, MD5Password);
        if (user == null)
            return ServerResponse.createByErrorMessage("密码错误");
        //如果获得了用户，那么就将用户密码置空，然后返回
        user.setPassword(StringUtils.EMPTY);
        //返回json序列化
        return ServerResponse.createBySuccess("登录成功", user);
    }

    /**
     * 用户注册
     * @param user 新用户
     * @return
     */
    public ServerResponse<String> register(User user) {
        //校验用户名是否存在
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        //校验失败，也就是用户名存在
        if (!validResponse.isSuccess())
            return validResponse;
        //校验邮箱是否存在
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        //校验失败，也就是邮箱已经注册
        if (!validResponse.isSuccess())
            return validResponse;
        //默认将用户设置为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //将新用户添加进数据库
        int resultCount = userMapper.insert(user);

        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("注册失败");
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 实时校验
     * @param str  文本框的字符串
     * @param type 文本框类型（用户名还是邮箱）
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type) {
        //判断传入的校验参数名称是否为空
        if (StringUtils.isNotBlank(type)){
            //开始校验
            //如果是用户名
            if (Const.USERNAME.equals(type)){
                //检查用户名是否存在
                if (userMapper.checkUsername(str) > 0)
                    return ServerResponse.createByErrorMessage("用户名已经存在");
            }
            //如果是邮箱
            if (Const.EMAIL.equals(type)){
                //检查邮箱是否注册
                if (userMapper.checkEmail(str) > 0)
                    return ServerResponse.createByErrorMessage("邮箱已经注册");
            }
        }else {
            //参数为空返回参数错误
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //返回校验成功
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 获取密码问题
     * @param username 用户名
     * @return
     */
    public ServerResponse<String> selectQuestion(String username){
        //先校验用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        //用户不存在
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //获取问题
        String question = userMapper.selectQuestionByUsername(username);
        //成功
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空");
    }

    /**
     * 验证密保问题的答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        //答案正确
        if (resultCount > 0){
            //根据UUID生成token，并将token返回,如果token失效了，就过了改密码的时间
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }

    /**
     * 重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        //校验返回的token是否为空
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token不能为空");
        }
        //校验用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        //用户不存在
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //判断token是否失效
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("有效期已过，请重新修改");
        }
        //如果token未失效且相等，更新密码
        if (StringUtils.equals(forgetToken, token)){
            String MD5password = MD5Util.MD5EncodeUtf8(passwordNew);
            //根据用户名修改密码
            int count = userMapper.updatePasswordByUsername(username, MD5password);
            if (count > 0)
                return ServerResponse.createBySuccessMessage("修改密码成功");
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录状态重置密码
     * @param passwordOld 旧密码，防止横向越权
     * @param passwordNew 新密码
     * @param user 已登录用户
     * @return
     */
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        //校验旧密码是否统一，防止横向越权
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("旧密码错误");
        //更新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0)
            return ServerResponse.createBySuccessMessage("密码更新成功");
        return ServerResponse.createByErrorMessage("密码更新失败");

    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也必须进行校验，校验emil是否存在
        //email如果存在，不能是当前用户的，也就是查看是否email已经使用过
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0)
            return ServerResponse.createByErrorMessage("email已经注册");
        //重新建立一个user，去掉username字段，然后更新数据库
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        //更新user信息
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount == 0) {
            return ServerResponse.createByErrorMessage("更新信息失败");
        }
        return ServerResponse.createBySuccess("更新信息成功",updateUser);
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return
     */
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null)
            return ServerResponse.createByErrorMessage("找不到当前用户");
        //将用户密码置空，返回用户信息
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    ///////////////////////////////backend/////////////////////////
    /**
     * 检查是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN)
            return ServerResponse.createBySuccess();
        return ServerResponse.createByError();
    }

}

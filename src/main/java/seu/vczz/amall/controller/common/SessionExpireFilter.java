package seu.vczz.amall.controller.common;

import org.apache.commons.lang3.StringUtils;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisShardedPoolUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * CREATE by vczz on 2018/4/18
 * session过滤，主要是拦截每一个.do请求，然后重新设置session的有效期
 */
public class SessionExpireFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //第一步肯定是强转啦
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if (StringUtils.isNotEmpty(loginToken)){
            //如果loginToken不为空，则取出user信息
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            //拿到user
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user != null){
                //如果user不为空，则重新设置sessionId--user的时间
                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}

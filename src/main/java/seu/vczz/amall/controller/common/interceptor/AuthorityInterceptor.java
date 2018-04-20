package seu.vczz.amall.controller.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.RedisShardedPoolUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * CREATE by vczz on 2018/4/20
 * 拦截器，实现权限管理
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle------------------");
        //拿到请求controller中的方法
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //解析HandlerMethod
        //方法名
        String methodName = handlerMethod.getMethod().getName();
        //类名
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //解析请求参数，打印日志
        StringBuffer stringBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        //迭代，拿到具体的key和value
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String mapKey = (String) entry.getKey();

            String mapValue = StringUtils.EMPTY;
            //先用object接住
            Object obj = entry.getValue();
            if (obj instanceof String[]){
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            stringBuffer.append(mapKey).append("=").append(mapValue);
        }
        //判断拦截到的是否是登录请求
        if (StringUtils.equals("UserManageController", className)&&StringUtils.equals("login", methodName)){
            log.info("拦截器拦截到登录请求");
            //直接返回true
            return true;
        }
        log.info("拦截到请求：{}", stringBuffer.toString());

        //拿到获取用户的代码
        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        //拿到用户
        if (StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }
        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            //如果用户没有登录或者是用户没有管理员权限,则应该是返回false的，也就是不会调用controller中的方法
            //由于不能调用controller的方法了，但是要给前端返回response，该方法只能返回布尔，因此重写下response，必须reset
            //相当于使用重写的response而重写了mvc的返回流程
            response.reset();
            response.setCharacterEncoding("UTF-8");//必须
            response.setContentType("application/json;charset=UTF-8");//必须

            PrintWriter out = response.getWriter();
            if (user == null){
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截，用户未登录")));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截，无权限")));
            }
            out.flush();
            out.close();//关闭
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle------------------");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion-------------");
    }
}

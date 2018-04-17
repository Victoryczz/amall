package seu.vczz.amall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CREATE by vczz on 2018/4/17
 * Cookie工具
 */
@Slf4j
public class CookieUtil {
    //domain意思是范围，领域；代表cookie的范围，当前代表只要域名是.amall.com下的二级域名都可以使用该cookie
    private final static String COOKIE_DOMAIN = "amall.com";
    //cookie的名字
    private final static String COOKIE_NAME = "amall_login_token";

    /**
     * 在响应中写入cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token){
        //新cookie，名称为amall_login_name,value为token
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        //设置cookie范围
        cookie.setDomain(COOKIE_DOMAIN);
        //代表domain设置在根目录,如A.amall.com/;
        //该设置和domain是一一对应的，url分为domain和path两个部分，都代表其他地址是否能够访问到的cookie
        cookie.setPath("/");
        //单位是秒，设置为-1代表永久有效，当不设置该属性，则不会写入硬盘，而是写在内存，只在当前浏览器有效，关闭即失效
        cookie.setMaxAge(60*60*24);
        //防止脚本攻击
        cookie.setHttpOnly(true);
        log.info("write cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
        //由于使用了tomcat9，所以这里一直报错，是由于tomcat8.5之后domain规则改变导致的
        response.addCookie(cookie);
    }

    /**
     * 读cookie,获取token
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request){
        //先取出所有cookie
        Cookie[] cookies = request.getCookies();
        //遍历找cookie
        if (cookies != null){
            for (Cookie cookie : cookies){
                log.info("read cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                //如果cookie的名字和amall_login_token相等
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 删除token
     * @param request
     * @param response
     */
    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);//设为0代表删除该cookie
                    log.info("del cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
        return;
    }

}

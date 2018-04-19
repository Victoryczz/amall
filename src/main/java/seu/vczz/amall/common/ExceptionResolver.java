package seu.vczz.amall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CREATE by vczz on 2018/4/19
 * 全局异常处理
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} exception: ", httpServletRequest.getRequestURI(), e);
        //点进去看ModelAndView，之所以使用MappingJacksonJsonView，是因为pom中jackson使用的不是2.0版本及以上
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
        modelAndView.addObject("status:", ResponseCode.ERROE.getCode());
        modelAndView.addObject("msg:", "接口异常，请查看后端异常信息");
        modelAndView.addObject("data", e.toString());
        return modelAndView;
    }
}

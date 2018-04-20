package seu.vczz.amall.controller.backend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.service.IOrderService;

/**
 * CREATE by vczz on 2018/4/10
 * 后台订单管理
 */
@Controller
@RequestMapping(value = "/manage/order/")
public class OrderManageController {


    @Autowired
    private IOrderService iOrderService;

    /**
     * 获得订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse orderList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //权限交给拦截器
        return iOrderService.getManageOrderList(pageNum, pageSize);
    }

    /**
     * 后台获取订单详情
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse orderDetail(Long orderNo){
        //权限交给拦截器
        return iOrderService.getManageOrderDetail(orderNo);


    }

    /**
     * 查找订单，当前只用了精确查找，且分页
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse orderSearch(Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //权限交给拦截器
        return iOrderService.searchOrder(orderNo, pageNum, pageSize);
    }

    /**
     * 后台发货
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "sendGoods.do")
    @ResponseBody
    public ServerResponse sendGoods(Long orderNo){
        //权限交给拦截器
        return iOrderService.sendGoods(orderNo);
    }


}

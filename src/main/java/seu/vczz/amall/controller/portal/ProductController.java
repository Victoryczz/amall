package seu.vczz.amall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.service.IProductService;

/**
 * CREATE by vczz on 2018/4/9
 * 前台产品,如查看产品详情等功能
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;


    /**
     * 前台获得产品详情
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    /**
     * 根据分类id或者关键词获取产品
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "orderBy", defaultValue = "") String orderBy ){
        return iProductService.list(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}

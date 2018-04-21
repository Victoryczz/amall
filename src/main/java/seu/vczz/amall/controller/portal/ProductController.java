package seu.vczz.amall.controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    //拷贝一份，改造至RESTful格式，注意请求路径、@PathVariable注解
    @RequestMapping(value = "{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse detailRESTful(@PathVariable Integer productId){
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

    //list.do方法RESTful风格改造,这种是不行的，因为categoryId和keyword必须传递，
    // 而我们的实现中是两者之一即可，所以需要更加详细的资源标识
    //注意即使pageNum、pageSize等可以不传，也必须传递（由前端构造默认值），因为PathVariable注解没有default属性
    @RequestMapping(value = "/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse listRESTful(@PathVariable(value = "keyword") String keyword,
                               @PathVariable(value = "categoryId") Integer categoryId,
                               @PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               @PathVariable(value = "orderBy") String orderBy ){
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }
        return iProductService.list(keyword, categoryId, pageNum, pageSize, orderBy);
    }

    //针对只传递关键字的改造
    @RequestMapping(value = "keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse listRESTful(@PathVariable(value = "keyword") String keyword,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @PathVariable(value = "orderBy") String orderBy ){
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }

        return iProductService.list(keyword, null, pageNum, pageSize, orderBy);
    }
    //针对只传递categoryId的改造
    @RequestMapping(value = "categoryId/{categoryId}/{pageNum}/{pageSize}/{orderBy}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse listRESTful(@PathVariable(value = "categoryId") Integer categoryId,
                                      @PathVariable(value = "pageNum") Integer pageNum,
                                      @PathVariable(value = "pageSize") Integer pageSize,
                                      @PathVariable(value = "orderBy") String orderBy ){
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }

        return iProductService.list(null, categoryId, pageNum, pageSize, orderBy);
    }
}

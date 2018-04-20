package seu.vczz.amall.controller.backend;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.service.IFileService;
import seu.vczz.amall.service.IProductService;
import seu.vczz.amall.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * CREATE by vczz on 2018/4/9
 * 后端商品管理模块
 */
@Controller
@RequestMapping(value = "/manage/product/")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品，新增或者更新使用同一个接口
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(Product product){
        return iProductService.saveOrUpdateProduct(product);
    }

    /**
     * 更新产品状态：上架或下架
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(Integer productId, Integer status){
        return iProductService.setSaleStatus(productId, status);
    }

    /**
     * 获得产品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(Integer productId){
        return iProductService.manageProductDetail(productId);
    }

    /**
     * 获取产品列表
     * @param pageNum  页码
     * @param pageSize 条数
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return iProductService.getProductList(pageNum, pageSize);
    }

    /**
     * 搜索，通过产品名称或者产品ID
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse searchProduct(String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 上传文件
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest request ,@RequestParam("upload_file")MultipartFile file){
        //权限拦截交给拦截器
        String path = request.getSession().getServletContext().getRealPath("upload");
        //targetFileName是tomcat的绝对路径下的文件名，不是全名
        String targetFileName = iFileService.upload(file, path);
        //此处应是ftp下放置文件的位子
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);

    }

    /**
     * 富文本上传
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        //权限拦截交给拦截器
        Map resultMap = Maps.newHashMap();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file,path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg","上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg","上传成功");
        resultMap.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }

}

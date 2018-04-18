package seu.vczz.amall.controller.backend;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.IFileService;
import seu.vczz.amall.service.IProductService;
import seu.vczz.amall.service.IUserService;
import seu.vczz.amall.util.CookieUtil;
import seu.vczz.amall.util.JsonUtil;
import seu.vczz.amall.util.PropertiesUtil;
import seu.vczz.amall.util.RedisPoolUtil;
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
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品，新增或者更新使用同一个接口
     * @param request
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest request, Product product){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能新增产品");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("用户没有管理员权限");
        }
    }

    /**
     * 更新产品状态：上架或下架
     * @param request
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request, Integer productId, Integer status){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能设置产品状态");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //设置状态
            return iProductService.setSaleStatus(productId, status);
        }else {
            return ServerResponse.createBySuccessMessage("当前用户没有权限");
        }
    }

    /**
     * 获得产品详情
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest request, Integer productId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取产品详情");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户没有权限");
        }
    }

    /**
     * 获取产品列表
     * @param request
     * @param pageNum  页码
     * @param pageSize 条数
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能获取产品列表");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //动态分页
            return iProductService.getProductList(pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMessage("当前用户没有权限");
        }
    }

    /**
     * 搜索，通过产品名称或者产品ID
     * @param request
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpServletRequest request, String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能查找产品");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //动态分页
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMessage("当前用户没有权限");
        }
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
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,不能上传文件");
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "尚未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            //targetFileName是tomcat的绝对路径下的文件名，不是全名
            String targetFileName = iFileService.upload(file, path);
            //此处应是ftp下放置文件的位子
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByErrorMessage("没有权限");
        }

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
        Map resultMap = Maps.newHashMap();
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        //重构
        //拿到loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        //判断cookie 是否为空
        if (StringUtils.isEmpty(loginToken)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //转user
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
        //        {
        //            "success": true/false,
        //                "msg": "error message", # optional
        //            "file_path": "[real file path]"
        //        }
        if(iUserService.checkAdminRole(user).isSuccess()){
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
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }

}

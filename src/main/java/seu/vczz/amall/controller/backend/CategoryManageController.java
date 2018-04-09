package seu.vczz.amall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.User;
import seu.vczz.amall.service.ICategoryService;
import seu.vczz.amall.service.IUserService;

import javax.servlet.http.HttpSession;

/**
 * CREATE by vczz on 2018/4/9
 * 分类管理,通过检验用户是否管理员来避免纵向越权
 */
@Controller
@RequestMapping(value = "/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 先校验用户权限，然后添加分类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,@RequestParam(value = "parentId", defaultValue = "0")int parentId){
        //先判断用户是否为空
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        //检验是不是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //增加处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        }else {
            return ServerResponse.createByErrorMessage("该用户没有权限");
        }
    }

    /**
     * 更新分类名称,此处是传进去的id，我绝得是前端控制id，因为后台查到分类之后是由id的，当你编辑时将id传进来即可，不会导致id出错
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_categoryName.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //增加处理分类的逻辑
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }else {
            return ServerResponse.createByErrorMessage("没有权限");
        }

    }

    /**
     * 获取子节点平级的category
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 获取递归子节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点的id
            return iCategoryService.getCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }


}

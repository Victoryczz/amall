package seu.vczz.amall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.service.ICategoryService;

/**
 * CREATE by vczz on 2018/4/9
 * 分类管理,通过检验用户是否管理员来避免纵向越权
 */
@Controller
@RequestMapping(value = "/manage/category/")
public class CategoryManageController {

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 先校验用户权限，然后添加分类
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0")int parentId){
        //权限操作全部交给拦截器
        return iCategoryService.addCategory(categoryName, parentId);
    }

    /**
     * 更新分类名称,此处是传进去的id，我绝得是前端控制id，因为后台查到分类之后是由id的，当你编辑时将id传进来即可，不会导致id出错
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_categoryName.do")
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId, String categoryName){
        //权限操作交给拦截器
        return iCategoryService.updateCategoryName(categoryId, categoryName);

    }

    /**
     * 获取子节点平级的category
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        //权限操作交给拦截器
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 获取递归子节点
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        //权限操作交给拦截器
        return iCategoryService.getCategoryAndChildrenById(categoryId);
    }


}

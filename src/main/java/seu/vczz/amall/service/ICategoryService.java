package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Category;

import java.util.List;

/**
 * CREATE by vczz on 2018/4/9
 * 分类管理服务
 */
public interface ICategoryService {

    /**
     * 添加分类
     * @param categoryName
     * @param parentId
     * @return
     */
    ServerResponse addCategory(String categoryName, Integer parentId);

    /**
     * 更新分类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 获得当前分类的子分类
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    /**
     * 递归查询子分类
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);

}

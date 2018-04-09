package seu.vczz.amall.service;

import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.pojo.Category;

import java.util.List;

/**
 * CREATE by vczz on 2018/4/9
 * 分类管理服务
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);

}

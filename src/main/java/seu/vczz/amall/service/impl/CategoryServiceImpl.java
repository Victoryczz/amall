package seu.vczz.amall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.dao.CategoryMapper;
import seu.vczz.amall.pojo.Category;
import seu.vczz.amall.service.ICategoryService;

import java.util.List;
import java.util.Set;

/**
 * CREATE by vczz on 2018/4/9
 * 分类管理服务实现
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
    //日志,注释掉使用lombok注解
    //private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     * @param categoryName 分类名称
     * @param parentId 父类id
     * @return
     */
    public ServerResponse addCategory(String categoryName, Integer parentId){
        //如果父类id为空或分类名称为空
        if (parentId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMessage("添加分类参数错误");

        //创建一个分类
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //代表当前分类可使用
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0)
            return ServerResponse.createBySuccessMessage("添加品类成功");
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 更新category的名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName){
        //先检验
        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMessage("更新参数错误");
        //创建分类
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        //根据id有选择性的更新
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0)
            return ServerResponse.createBySuccessMessage("更新分类成功");
        return ServerResponse.createByErrorMessage("更新分类失败");
    }
    /**
     * 找到当前分类的所有平级子分类
     * @param categoryId 分类id
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        //根据分类id，查询所有以该id为父类id的分类
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当亲分类的子分类");//这是因为使用lombok @slf4j注解
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归获取所有子节点及本节点
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = this.findChildCategory(categorySet, categoryId);
        //放分类id
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            for (Category category : categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，获取所有子分类，使用set存储，避免重复，所以重写了hashcode和equals,他这方法查了两次，不好
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        //当前分类
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //查找子节点
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList){
            //递归过程中，传递的都是categorySet
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }


}

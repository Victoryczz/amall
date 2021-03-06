package seu.vczz.amall.dao;

import org.apache.ibatis.annotations.Param;
import seu.vczz.amall.pojo.Product;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName, @Param("productId")Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList")List<Integer> categoryIdList);

    //获得产品的库存
    Integer selectStockByProductId(Integer productId);
}
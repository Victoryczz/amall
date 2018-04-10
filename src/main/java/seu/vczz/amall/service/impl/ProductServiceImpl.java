package seu.vczz.amall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.dao.CategoryMapper;
import seu.vczz.amall.dao.ProductMapper;
import seu.vczz.amall.pojo.Category;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.service.ICategoryService;
import seu.vczz.amall.service.IProductService;
import seu.vczz.amall.util.DateTimeUtil;
import seu.vczz.amall.util.PropertiesUtil;
import seu.vczz.amall.vo.ProductDetailVo;
import seu.vczz.amall.vo.ProductListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATE by vczz on 2018/4/9
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 更新或新增产品，都使用一个函数
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){
            //将子图的第一个图赋值给主图
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            //id不为空，证明是要更新而不是插入
            if (product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount == 0){
                    return ServerResponse.createByErrorMessage("产品更新失败");
                }
                return ServerResponse.createBySuccessMessage("产品更新成功");
            }
            //id是空的
            int rowCount = productMapper.insert(product);
            if (rowCount == 0){
                return ServerResponse.createBySuccessMessage("产品新增失败");
            }
            return ServerResponse.createBySuccessMessage("产品新增成功");

        }
        return ServerResponse.createByErrorMessage("产品参数错误");
    }

    /**
     * 设置产品状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse setSaleStatus(Integer productId, Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "更新参数错误");
        }
        //使用selective的方式更新
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount == 0){
            return ServerResponse.createByErrorMessage("更新状态失败");
        }
        return ServerResponse.createBySuccessMessage("更新状态成功");
    }

    /**
     * 获取产品详细信息,使用view object
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "产品参数错误");
        }
        Product product =  productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("获取产品详情失败");
        //返回vo对象，一期可以暂时理解为value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    //装配产品详情vo
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setName(product.getName());
        productDetailVo.setStocks(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        //imageHost,可以从配置文件中获取，从而与代码分离
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategoryId(0);//默认设置为根节点
        }
        Integer parentCategoryId = category.getParentId();
        productDetailVo.setParentCategoryId(parentCategoryId);
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 获取产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getProductList(int pageNum, int pageSize){
        //使用分页插件
        //1.startPage  2.填充sql  3.pageHelper收尾
        PageHelper.startPage(pageNum, pageSize);
        //先获取产品列表
        List<Product> productList = productMapper.selectList();
        //创建voList
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //这一步应该该是冗余了，可以直接调用空构造，然后setList
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    //装配产品列表的vo
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setPrice(product.getPrice());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    /**
     * 根据名字或id查询商品
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        //查询
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);

        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //这一步应该该是冗余了，可以直接调用空构造，然后setList
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 前台获取产品详情
     * @param productId
     * @return
     */
    public ServerResponse getProductDetail(Integer productId){
        if (productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架");
        }
        //装配获得的产品
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 根据关键词或分类id获取产品列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    public ServerResponse list(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy){
        if (StringUtils.isBlank(keyword) && categoryId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //查询产品时候，应该是所以子分类及当前产品，所以用到了递归查询
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if (categoryId != null){
            //如果分类id不为空，查询分类
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //如果根据分类id未命中产品而且关键字为空，不是返回错误而是返回空集
            if (category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //否则，调用categoryService的方法递归子节点
            categoryIdList = iCategoryService.getCategoryAndChildrenById(category.getId()).getData();
        }
        //如果关键字不为空
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                //对orderBy进行一个分割
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        //再根据关键字和categroyId查询产品
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword, categoryIdList.size() == 0?null:categoryIdList);
        //再转为Vo
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}

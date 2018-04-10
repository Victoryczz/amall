package seu.vczz.amall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.Const;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.dao.CartMapper;
import seu.vczz.amall.dao.ProductMapper;
import seu.vczz.amall.pojo.Cart;
import seu.vczz.amall.pojo.Product;
import seu.vczz.amall.service.ICartService;
import seu.vczz.amall.util.BigDecimalUtil;
import seu.vczz.amall.util.PropertiesUtil;
import seu.vczz.amall.vo.CartProductVo;
import seu.vczz.amall.vo.CartVo;
import java.math.BigDecimal;
import java.util.List;

/**
 * CREATE by vczz on 2018/4/10
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加商品到购物车
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    public ServerResponse add(Integer userId, Integer count, Integer productId){
        //判断传参
        if (userId == null || productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查看当前购物车是否有该商品
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null){
            //当前购物车中没有这个商品，需要新增然后插入
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
            //新增
            cartMapper.insert(cartItem);
        }else {
            //产品已经存在，只增加数量即可，然后更新数据库
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //构造cart VO
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);

    }

    /**
     * 更新购物车中商品的数量，该函数不好，每次添加一个商品，都会重新构造cart VO，没有更新数量的产品也会被查询
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse update(Integer userId, Integer productId, Integer count){
        //判断入参
        if (productId == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //获取cart
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //cart VO
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 删除购物车商品
     * @param userId
     * @param productIds
     * @return
     */
    public ServerResponse deleteProduct(Integer userId, String productIds){
        if (userId == null || productIds == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //guawa
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (productIdList == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteCartByUserIdProductId(userId, productIdList);
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 查询购物车，其实就是点进购物车时，其他方法都是要查询购物车的
     * @param userId
     * @return
     */
    public ServerResponse list(Integer userId){
        if (userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 选中或是不选
     * @param userId
     * @param checked
     * @return
     */
    public ServerResponse selectOrUnSelect(Integer userId, Integer checked, Integer productId){
        cartMapper.checkedOrUncheckedProduct(userId, checked, productId);
        return list(userId);
    }

    /**
     * 获得购物车中产品的数量
     * @param userId
     * @return
     */
    public ServerResponse getCartProductCount(Integer userId){
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }




    /**
     * 返回一个cartVo类，是购物车类，该类中包含一个cartProductVoList，该cartProductVo是通过cart以及product共同得到
     * 因为cart中不足以表示这么多数据，product又不足以表明属于谁
     * 所以cart&product-->cartProductVo-->cartProductVoList-->cartVo-->server response
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //先获得购物车条目的列表
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //这个构造器是很关键的，解决丢失精度的问题
        BigDecimal totalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            //遍历，将cart转化为cartProductVo
            for (Cart cartItem : cartList){
                //购物车中商品的一个vo
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                //拿到商品
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存数量是不是大于添加的数量
                    int limitNum = 0;
                    //如果商品的库存数量大于购物车中添加的数量，则是限制成功
                    if (product.getStock() >= cartItem.getQuantity()){
                        cartProductVo.setQuantity(cartItem.getQuantity());
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        //否则限制失败，只能添加商品的总数个
                        limitNum = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAILED);
                        cartProductVo.setQuantity(cartItem.getQuantity());
                        //限制失败则更新cart中有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setQuantity(limitNum);
                        cartForQuantity.setId(cartItem.getId());
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.multi(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());

                }
                //如果已经勾选，增加到总价中
                if (cartItem.getChecked() == Const.Cart.CHECKED){
                    totalPrice = BigDecimalUtil.add(cartProductVo.getProductTotalPrice().doubleValue(), totalPrice.doubleValue());
                }
                //添加
                cartProductVoList.add(cartProductVo);
            }
        }
        //构造cartVO
        cartVo.setCartProductVoList(cartProductVoList);//设置cartProductVoList
        cartVo.setCartTotalPrice(totalPrice);//设置总价
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));//设置图片路径前缀,同product中的image一拼接就可以拿到图片了
        cartVo.setAllChecked(getAllCheckedStatus(userId));//判断是否是全部选中的状态

        return cartVo;
    }
    //获得用户的购物车是否是全选的状态
    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null)
            return false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }


}

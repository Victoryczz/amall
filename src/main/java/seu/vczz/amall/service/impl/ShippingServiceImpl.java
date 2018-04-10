package seu.vczz.amall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seu.vczz.amall.common.ResponseCode;
import seu.vczz.amall.common.ServerResponse;
import seu.vczz.amall.dao.ShippingMapper;
import seu.vczz.amall.pojo.Shipping;
import seu.vczz.amall.service.IShippingService;

import java.util.List;
import java.util.Map;

/**
 * CREATE by vczz on 2018/4/10
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 新增地址信息
     * @param userId
     * @param shipping
     * @return
     */
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        //已经改了xml，需要返回自动生成的主键id
        if (rowCount > 0){
            //使用一个map来存，作为返回
            Map resultMap = Maps.newHashMap();
            resultMap.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", resultMap);
        }
        return ServerResponse.createByErrorMessage("添加地址失败");
    }

    /**
     * 按理说只需要shippingId即可实现删除，但是传了userId就是为了避免横向越权
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse delete(Integer userId, Integer shippingId){
        if (userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * 更新收获地址
     * @param userId
     * @param shipping
     * @return
     */
    public ServerResponse update(Integer userId, Shipping shipping){
        if (userId == null || shipping == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //再设置一下userId，双重id监测避免横向越权
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新收获地址成功");
        }
        return ServerResponse.createByErrorMessage("更新收获地址失败");

    }

    /**
     * 查询地址信息
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse select(Integer userId, Integer shippingId){
        if (userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if (shipping == null){
            return ServerResponse.createByErrorMessage("获取地址失败");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    /**
     * 查询地址列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);

    }

}

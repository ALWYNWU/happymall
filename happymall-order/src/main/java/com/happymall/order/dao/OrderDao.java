package com.happymall.order.dao;

import com.happymall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 14:54:40
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("tradeNo") String tradeNo, @Param("code") Integer code);

}

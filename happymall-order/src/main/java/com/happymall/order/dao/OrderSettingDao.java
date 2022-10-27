package com.happymall.order.dao;

import com.happymall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 14:54:41
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}

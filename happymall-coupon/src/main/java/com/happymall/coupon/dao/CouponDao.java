package com.happymall.coupon.dao;

import com.happymall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-08-18 00:48:15
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

package com.happymall.product.dao;

import com.happymall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectIndexedAttrIds(@Param("attrIds") List<Long> attrIds);
}

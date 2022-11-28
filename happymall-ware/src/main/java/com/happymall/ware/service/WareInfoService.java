package com.happymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.happymall.ware.entity.WareInfoEntity;
import com.happymall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}


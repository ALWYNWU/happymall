package com.happymall.flashsale.to;

import com.happymall.flashsale.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2023-01-10 11:04 p.m.
 * @description
 */

@Data
public class SecKillSkuRedisTo {

    private Long id;

    private Long promotionId;

    private Long promotionSessionId;

    private Long skuId;

    private String randomCode;

    private BigDecimal seckillPrice;

    private Integer seckillCount;

    private Integer seckillLimit;

    private Integer seckillSort;

    private SkuInfoVo skuInfo;

    private Long startTime;
    private Long endTime;

}

package com.happymall.flashsale.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2023-01-06 12:29 a.m.
 * @description
 */
@Data
public class SeckillSkuVo {

    private Long id;

    private Long promotionId;

    private Long promotionSessionId;

    private Long skuId;

    private BigDecimal seckillPrice;

    private BigDecimal seckillCount;

    private BigDecimal seckillLimit;

    private Integer seckillSort;

}

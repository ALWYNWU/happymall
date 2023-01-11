package com.happymall.flashsale.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2023-01-10 11:07 p.m.
 * @description
 */
@Data
public class SkuInfoVo {

    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;

    private String skuName;

    private String skuDesc;

    private Long catalogId;

    private Long brandId;

    private String skuDefaultImg;

    private String skuTitle;

    private String skuSubtitle;

    private BigDecimal price;

    private Long saleCount;

}

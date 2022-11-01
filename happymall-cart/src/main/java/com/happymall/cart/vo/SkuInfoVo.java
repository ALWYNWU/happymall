package com.happymall.cart.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2022-10-20 9:17 p.m.
 * @description
 */

@Data
public class SkuInfoVo {


    @TableId
    private Long skuId;

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

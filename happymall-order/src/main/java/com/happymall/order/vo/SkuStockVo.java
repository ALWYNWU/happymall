package com.happymall.order.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-23 4:42 p.m.
 * @description
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}

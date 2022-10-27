package com.happymall.ware.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-24 12:01 a.m.
 * @description
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}

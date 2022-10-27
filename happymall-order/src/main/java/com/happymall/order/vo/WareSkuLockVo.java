package com.happymall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-23 11:57 p.m.
 * @description
 */
@Data
public class WareSkuLockVo {

    // Order number
    private String orderSn;

    // stock lock info
    private List<OrderItemVo> locks;

}

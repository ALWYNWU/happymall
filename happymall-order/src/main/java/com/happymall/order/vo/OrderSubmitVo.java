package com.happymall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2022-10-23 8:04 p.m.
 * @description
 */

@Data
public class OrderSubmitVo {

    private Long addrId;
    private Integer payType;

    private String orderToken;
    private BigDecimal payPrice;

    // User info will store in session

}

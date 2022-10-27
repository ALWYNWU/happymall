package com.happymall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-22 11:35 p.m.
 * @description
 */

@Data
public class OrderItemVo {

    private Long skuId;
    private String title;
    private String image;
    private List<String> attrs;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;



}

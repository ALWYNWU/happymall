package com.happymall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Yilong
 * @date 2022-10-22 11:26 p.m.
 * @description
 */

// Order confirmation page data

public class OrderConfirmVo {

    // Addresses
    @Getter @Setter
    List<MemberAddressVo> address;

    // Checked items
    @Getter @Setter
    List<OrderItemVo> items;

    // Invoice

    // Gift Card Balance
    @Getter @Setter
    Integer integration;

    // Prevent place order repeatedly
    @Getter @Setter
    String orderToken;

    @Getter @Setter
    Map<Long, Boolean> stocks;

    public Integer getCount() {
        Integer i = 0;
        if (items != null && items.size() > 0){
            for (OrderItemVo item : items) {
                i = i + item.getCount();
            }
        }
        return i;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items != null){
            for (OrderItemVo item : items) {
                sum = sum.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString())));
            }
        }
        return sum;
    }

//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}

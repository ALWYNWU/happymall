package com.happymall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2022-10-23 6:16 p.m.
 * @description
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}

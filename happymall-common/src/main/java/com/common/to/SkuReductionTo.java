package com.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-06 6:51 p.m.
 * @description
 */

@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;



}

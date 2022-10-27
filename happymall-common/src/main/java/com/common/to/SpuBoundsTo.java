package com.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yilong
 * @date 2022-10-06 5:54 p.m.
 * @description
 */
@Data
public class SpuBoundsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}

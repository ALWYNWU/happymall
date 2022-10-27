package com.happymall.order.vo;

import com.happymall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-23 8:30 p.m.
 * @description
 */

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code;

}

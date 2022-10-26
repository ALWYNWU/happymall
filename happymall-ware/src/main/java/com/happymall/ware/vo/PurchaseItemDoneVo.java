package com.happymall.ware.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-07 9:32 p.m.
 * @description
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;

}

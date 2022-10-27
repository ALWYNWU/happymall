package com.happymall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-07 9:30 p.m.
 * @description
 */
@Data
public class PurchaseFinishVo {

    @NotNull
    private Long id;

    private List<PurchaseItemDoneVo> items;

}

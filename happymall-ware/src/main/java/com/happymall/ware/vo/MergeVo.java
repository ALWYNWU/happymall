package com.happymall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-07 3:45 p.m.
 * @description
 */

@Data
public class MergeVo {

    private Long purchaseId;
    private List<Long> items;

}

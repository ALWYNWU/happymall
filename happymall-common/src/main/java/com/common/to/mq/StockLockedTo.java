package com.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-24 7:05 p.m.
 * @description
 */

@Data
public class StockLockedTo {

    private Long id; // Ware order task id
    private StockDetailTo detailTo; // Detail ids

}

package com.happymall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-13 9:29 p.m.
 * @description
 */

@Data
public class SearchParam {

    private String keyword;

    private Long catalog3Id;

    /**
     * sort=saleCount_asc/desc
     */
    private String sort;

    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    private Integer pageNum = 1;

    private String _queryString;


}

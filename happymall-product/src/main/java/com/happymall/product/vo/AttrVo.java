package com.happymall.product.vo;


import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-04 2:40 a.m.
 * @description
 */

@Data
public class AttrVo {


    private Long attrId;

    private String attrName;


    private Integer searchType;


    private String icon;


    private String valueSelect;


    private Integer attrType;


    private Long enable;


    private Long catelogId;

    private Integer showDesc;

    private Long attrGroupId;
}

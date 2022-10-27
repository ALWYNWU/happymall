package com.happymall.search.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-15 12:09 a.m.
 * @description
 */

@Data
public class AttrRespVo {

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

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;

    private Long valueType;

}




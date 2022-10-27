package com.happymall.product.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-04 11:47 a.m.
 * @description
 */
@Data
public class AttrResponseVo extends AttrVo {

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;

    private Long valueType;

}

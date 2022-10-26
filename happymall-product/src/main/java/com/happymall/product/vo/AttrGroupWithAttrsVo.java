package com.happymall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.happymall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-05 2:06 p.m.
 * @description
 */
@Data
public class AttrGroupWithAttrsVo {

    @TableId
    private Long attrGroupId;

    private String attrGroupName;

    private Integer sort;

    private String descript;

    private String icon;

    private Long catelogId;

    private List<AttrEntity> attrs;

}

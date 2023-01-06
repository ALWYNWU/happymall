package com.happymall.flashsale.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Yilong
 * @date 2023-01-06 12:30 a.m.
 * @description
 */
@Data
public class SeckillSessionsWithSkus {

    private Long id;

    private String name;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Date createTime;

    private List<SeckillSkuVo> relationEntities;

}

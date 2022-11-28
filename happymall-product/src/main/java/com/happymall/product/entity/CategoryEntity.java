package com.happymall.product.entity;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@TableId
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long catId;

	private String name;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long parentCid;

	private Integer catLevel;

	@TableLogic(value = "1", delval = "0")
	private Integer showStatus;

	private Integer sort;

	private String icon;

	private String productUnit;

	private Integer productCount;

	/**
	 * No column in database
	 * @JsonInclude(JsonInclude.Include.NON_EMPTY) Contain this field when it is not empty
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntity> children;

}

package com.happymall.search.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品三级分类
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@Data
@TableName("pms_category")
public class CategoryEntityVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 0: No display, 1: Display
	 */
	@TableLogic(value = "1", delval = "0")
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;

	/**
	 * No column in database
	 * @JsonInclude(JsonInclude.Include.NON_EMPTY) Contain this field when it is not empty
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntityVo> children;

}

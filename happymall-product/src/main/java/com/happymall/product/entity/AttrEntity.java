package com.happymall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@Data
@TableName("pms_attr")
public class AttrEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@TableId()
	private Long attrId;

	private String attrName;

	private Integer searchType;

	private String icon;

	private String valueSelect;

	private Integer attrType;

	private Long enable;

	private Long catelogId;

	private Integer showDesc;

}

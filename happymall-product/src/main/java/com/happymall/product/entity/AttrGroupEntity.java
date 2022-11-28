package com.happymall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@TableId
	private Long attrGroupId;

	private String attrGroupName;

	private Integer sort;

	private String descript;

	private String icon;

	private Long catelogId;

	@TableField(exist = false)
	private Long[] catelogPath;

}

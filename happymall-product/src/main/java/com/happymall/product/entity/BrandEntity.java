package com.happymall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.common.validator.ListValue;
import com.common.validator.group.AddGroup;
import com.common.validator.group.UpdateGroup;
import com.common.validator.group.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * Brand
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Brand id
	 */
	@NotNull(message = "Edit brand must specify brand id!", groups = {UpdateGroup.class})
	@Null(message = "Create brand can not specify id!", groups = {AddGroup.class})
	@TableId
	private Long brandId;

	/**
	 * Brand name
	 */
	@NotBlank(message = "Brand name can not be empty!", groups = {AddGroup.class, UpdateGroup.class})
	private String name;

	@NotBlank(message = "Logo url can not be empty!", groups = {AddGroup.class})
	@URL(message = "Logo must be a valid url address", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;


	private String descript;

	@NotNull(groups = {AddGroup.class, UpdateGroup.class})
	@ListValue(vals = {0, 1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;


	@NotEmpty(message = "First letter can not be empty!", groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "First letter must be one letter", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;


	@NotNull(message = "Rank can not be empty!", groups = {AddGroup.class})
	@Min(value = 0, message = "Rank must greater than 0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}

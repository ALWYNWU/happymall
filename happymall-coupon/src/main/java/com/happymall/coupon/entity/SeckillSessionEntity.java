package com.happymall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-08-18 00:48:15
 */
@Data
@TableName("sms_seckill_session")
public class SeckillSessionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;

	private String name;

	private Date startTime;

	private Date endTime;

	private Integer status;

	private Date createTime;

}

package com.happymall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存工作单
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
@Data
@TableName("wms_ware_order_task")
public class WareOrderTaskEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;

	private Long orderId;

	private String orderSn;

	private String consignee;

	private String consigneeTel;

	private String deliveryAddress;

	private String orderComment;

	private Integer paymentWay;

	private Integer taskStatus;

	private String orderBody;

	private String trackingNo;

	private Date createTime;

	private Long wareId;

	private String taskComment;

}

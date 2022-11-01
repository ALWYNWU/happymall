package com.happymall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 会员收货地址
 * 
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 14:42:51
 */
@Data
@TableName("ums_member_receive_address")
public class MemberReceiveAddressEntity implements Serializable{
	private static final long serialVersionUID = 1L;


	@TableId
	private Long id;

	private Long memberId;

	private String name;

	private String phone;

	private String postCode;

	private String province;

	private String city;

	private String region;

	private String detailAddress;

	private String areacode;

	private Integer defaultStatus;

}

package com.happymall.order.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-22 11:28 p.m.
 * @description
 */
@Data
public class MemberAddressVo {


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

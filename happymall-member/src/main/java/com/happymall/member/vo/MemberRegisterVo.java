package com.happymall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author Yilong
 * @date 2022-10-17 6:15 p.m.
 * @description
 */
@Data
public class MemberRegisterVo {


    private String username;

    private String password;

    private String phone;

}

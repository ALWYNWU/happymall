package com.happymall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author Yilong
 * @date 2022-10-16 8:33 p.m.
 * @description
 */

@Data
public class UserRegisterVo {

    @NotEmpty(message = "Username can not be empty")
    @Length(min = 6, max = 18, message = "Username must contain 6 to 18 characters")
    private String username;

    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 18, message = "Password must contain 6 to 18 characters")
    private String password;

    @NotEmpty(message = "Phone number can not be empty")
    private String phone;

}

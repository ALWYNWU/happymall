package com.happymall.cart.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-19 8:45 p.m.
 * @description
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}

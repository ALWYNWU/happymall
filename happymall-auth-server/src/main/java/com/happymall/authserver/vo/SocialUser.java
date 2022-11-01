package com.happymall.authserver.vo;

import lombok.Data;

/**
 * @author Yilong
 * @date 2022-10-18 3:36 a.m.
 * @description
 */
@Data
public class SocialUser {
    /*{
        "access_token": "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",
        "token_type": "bearer",
        "scope": ""
    }*/
    private String access_token;
    private String token_type;
    private String scope;


}

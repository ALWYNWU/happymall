package com.common.exception;

/**
 * @author Yilong
 * @date 2022-10-02 11:51 p.m.
 * @description
 */
public enum BizCodeEnume {
    UNKNOWN_EXCEPTION(10000, "Unknown exception"),
    VALID_EXCEPTION(10001, "Parameter format verification failed"),
    PRODUCT_ACTIVATE_EXCEPTION(11000,"Product activate error!"),
    USER_EXIST_EXCEPTION(15001,"Username already exist"),
    PHONE_EXIST_EXCEPTION(15002,"Phone number exist"),
    NO_STOCK_EXCEPTION(21000,"Insufficient stock of products");

    private int code;
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }
}

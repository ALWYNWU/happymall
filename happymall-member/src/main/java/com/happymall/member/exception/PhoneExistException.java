package com.happymall.member.exception;

/**
 * @author Yilong
 * @date 2022-10-17 6:47 p.m.
 * @description
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("Phone number has been registered");
    }
}

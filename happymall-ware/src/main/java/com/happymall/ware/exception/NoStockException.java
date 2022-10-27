package com.happymall.ware.exception;

/**
 * @author Yilong
 * @date 2022-10-24 12:42 a.m.
 * @description
 */
public class NoStockException extends RuntimeException {
    private Long skuId;
    public NoStockException(Long skuId) {
        super("Id: "+skuId+" Do not have enough stock");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}

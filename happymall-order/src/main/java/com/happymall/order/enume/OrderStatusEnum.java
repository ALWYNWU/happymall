package com.happymall.order.enume;

public enum  OrderStatusEnum {
    CREATE_NEW(0,"Pending payment"), //待付款
    PAYED(1,"Payment confirm"), // 已付款
    SENDED(2,"Shipped"),  // 已发货
    RECIEVED(3,"Delivered"), // 已送达
    CANCLED(4,"Cancel"), // 取消
    SERVICING(5,"Returning"),
    SERVICED(6,"Returned");
    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

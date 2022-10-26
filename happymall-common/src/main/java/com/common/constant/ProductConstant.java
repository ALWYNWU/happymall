package com.common.constant;

/**
 * @author Yilong
 * @date 2022-10-04 8:42 p.m.
 * @description
 */
public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1, "Base attribute"), ATTR_TYPE_SALE(0, "Sale attribute");

        private int code;
        private String msg;

        AttrEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public enum StatusEnum{

        CREATED_SPU(0, "Created"),
        SPU_AVAILABLE(1, "Available"),
        SPU_UNAVAILABLE(2, "Unavailable");

        private int code;
        private String msg;

        StatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}

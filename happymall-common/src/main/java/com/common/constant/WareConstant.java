package com.common.constant;

/**
 * @author Yilong
 * @date 2022-10-07 4:19 p.m.
 * @description
 */
public class WareConstant {

    public enum PurchaseStatusEnum{
        CREATED(0, "Created"), ASSIGNED(1, "Assigned"),
        RECEIVED(2, "Received"), FINISHED(3, "Finished"),
        HASERROR(4,"Has error");

        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg){
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

    public enum PurchaseDetailsStatusEnum{
        CREATED(0, "Created"), ASSIGNED(1, "Assigned"),
        BUYING(2, "Buying"), FINISHED(3, "Finished"),
        HASERROR(4,"Has error");

        private int code;
        private String msg;

        PurchaseDetailsStatusEnum(int code, String msg){
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

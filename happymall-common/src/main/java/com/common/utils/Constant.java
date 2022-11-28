/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.common.utils;

import com.common.validator.group.QiniuGroup;
import com.common.validator.group.AliyunGroup;
import com.common.validator.group.QcloudGroup;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Mark sunlightcs@gmail.com
 */
public class Constant {

    public static final int SUPER_ADMIN = 1;

    public static final String PAGE = "page";

    public static final String LIMIT = "limit";

    public static final String ORDER_FIELD = "sidx";

    public static final String ORDER = "order";

    public static final String ASC = "asc";


    public enum MenuType {

        CATALOG(0),

        MENU(1),

        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public enum ScheduleStatus {

        NORMAL(0),

        PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public enum CloudService {

        QINIU(1, QiniuGroup.class),

        ALIYUN(2, AliyunGroup.class),

        QCLOUD(3, QcloudGroup.class);

        private int value;

        private Class<?> validatorGroupClass;

        CloudService(int value, Class<?> validatorGroupClass) {
            this.value = value;
            this.validatorGroupClass = validatorGroupClass;
        }

        public int getValue() {
            return value;
        }

        public Class<?> getValidatorGroupClass() {
            return this.validatorGroupClass;
        }

    }

}

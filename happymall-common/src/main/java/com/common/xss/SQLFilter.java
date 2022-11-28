/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.common.xss;

import com.common.exception.RRException;
import org.apache.commons.lang.StringUtils;
import java.util.IllegalFormatException;


/**
 * @author Mark sunlightcs@gmail.com
 */
public class SQLFilter {


    public static String sqlInject(String str) {
        if(StringUtils.isBlank(str)){
            return null;
        }

        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");


        str = str.toLowerCase();


        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};


        for(String keyword : keywords){
            if(str.indexOf(keyword) != -1){
                throw new RRException("Contains illegal characters!!!");
            }
        }

        return str;
    }
}

package com.happymall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Yilong
 * @date 2022-10-16 6:46 p.m.
 * @description
 */
@ConfigurationProperties(prefix = "happymall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer corePoolSize;
    private Integer maxSize;
    private Integer keepAliveTime;

}

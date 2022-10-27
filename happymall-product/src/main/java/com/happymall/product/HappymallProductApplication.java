package com.happymall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableCaching
@EnableFeignClients(basePackages = "com.happymall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.happymall.product.dao")
@SpringBootApplication
public class HappymallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallProductApplication.class, args);
    }

}

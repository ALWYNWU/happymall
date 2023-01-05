package com.happymall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableFeignClients(basePackages = "com.happymall.member.feign")
@EnableDiscoveryClient
@EnableRedisHttpSession
@SpringBootApplication
public class HappymallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallMemberApplication.class, args);
    }

}

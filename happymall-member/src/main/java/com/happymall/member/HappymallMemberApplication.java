package com.happymall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 怎样远程调用别的服务
 * 1. 引入open-feign
 * 2. 编写一个interface 告诉springCloud这个接口需要调用远程服务
 *      声明接口的每一个方法是调用哪个远程服务的哪一个请求
 * 3. 开启远程调用
 *
 */
@EnableFeignClients(basePackages = "com.happymall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession
=======

@SpringBootApplication
>>>>>>> 70dfac08787284ad44496d1cdd80f3e135a5c2fd
public class HappymallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallMemberApplication.class, args);
    }

}

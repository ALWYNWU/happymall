package com.happymall.ware;

<<<<<<< HEAD
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableFeignClients(basePackages = "com.happymall.ware.feign")
@EnableTransactionManagement
@MapperScan("com.happymall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
=======
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
>>>>>>> 70dfac08787284ad44496d1cdd80f3e135a5c2fd
public class HappymallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallWareApplication.class, args);
    }

}

package com.happymall.order;
<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> newBranch
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
<<<<<<< HEAD
=======

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

>>>>>>> 70dfac08787284ad44496d1cdd80f3e135a5c2fd
=======
>>>>>>> newBranch
@SpringBootApplication
public class HappymallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallOrderApplication.class, args);
    }

}

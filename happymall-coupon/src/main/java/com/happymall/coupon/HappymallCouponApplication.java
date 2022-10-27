package com.happymall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HappymallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallCouponApplication.class, args);
    }

}

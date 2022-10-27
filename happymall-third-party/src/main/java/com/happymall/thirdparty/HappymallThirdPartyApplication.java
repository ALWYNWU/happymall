package com.happymall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HappymallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappymallThirdPartyApplication.class, args);
    }

}

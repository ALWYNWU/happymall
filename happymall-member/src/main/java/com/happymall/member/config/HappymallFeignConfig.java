package com.happymall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Yilong
 * @date 2022-10-23 2:12 a.m.
 * @description
 */
@Configuration
public class HappymallFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // Use RequestContextHolder get request attributes
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null){
                    HttpServletRequest request = attributes.getRequest();

                    if (request != null){
                        // Sync request header information (Cookie")
                        String cookie = request.getHeader("Cookie");
                        // synchronous old request's cookie into new request
                        requestTemplate.header("Cookie",cookie);
                    }
                }
            }
        };
    }

}

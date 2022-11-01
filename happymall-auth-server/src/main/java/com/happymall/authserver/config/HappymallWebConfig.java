package com.happymall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Yilong
 * @date 2022-10-17 3:16 a.m.
 * @description
 */
@Configuration
public class HappymallWebConfig implements WebMvcConfigurer {
        /**·
         * 视图映射:发送一个请求，直接跳转到一个页面
         * @param registry
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/register.html").setViewName("register");
        }

}

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

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/register.html").setViewName("register");
        }

}

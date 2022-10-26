package com.happymall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author Yilong
 * @date 2022-09-28 9:37 p.m.
 * @description
 *
 * CORS:
 * Cross-Origin Resource Sharing (CORS) is an HTTP-header based mechanism that allows a server to
 * indicate any origins (domain, scheme, or port) other than its own from which a browser should permit
 * loading resources.
 */

@Configuration
public class HappymallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Config Cross-Origin Resource Sharing (CORS)
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");

        // Allow cookies
        corsConfiguration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);


    }

}

package com.happymall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Yilong
 * @date 2022-10-03 9:10 p.m.
 * @description
 */
@Configuration
@EnableTransactionManagement // Open transaction
@MapperScan("com.happymall.product.dao")
public class MyBatisConfig {

    // import page interceptor
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        // If the page number that request to access bigger than last page, true: return to first page
        paginationInterceptor.setOverflow(true);

        // Max rows in one page
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }

}

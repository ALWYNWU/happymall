package com.happymall.flashsale.shcedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Yilong
 * @date 2023-01-05 10:43 p.m.
 * @description
 */

@Slf4j
@Component
public class HelloSchedule {

    /**
     * 1. 最后一位不能是年
     * 2. 1-7 代表周一到周日
     */
    @Scheduled(cron = "* * * * * ?")
    public void hello(){
        log.info("hello...");
    }

}

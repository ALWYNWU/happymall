package com.happymall.flashsale.shcedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Yilong
 * @date 2023-01-05 11:21 p.m.
 * @description 秒杀商品定时上架
 * 上架最近三天需要秒杀的商品
 * 三天00：00：00 - 23：59：59
 */

@Service
@Slf4j
public class FlashSaleSkuSchedule {

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadFlashsaleSkuLatest3Days(){

    }

}

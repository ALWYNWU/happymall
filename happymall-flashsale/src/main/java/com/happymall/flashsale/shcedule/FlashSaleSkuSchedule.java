package com.happymall.flashsale.shcedule;

import com.happymall.flashsale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    FlashSaleService flashSaleService;

    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";

    public FlashSaleSkuSchedule(FlashSaleService flashSaleService,
                                RedissonClient redissonClient) {
        this.flashSaleService = flashSaleService;
        this.redissonClient = redissonClient;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadFlashsaleSkuLatest3Days(){

        //分布式锁 只需要一台机器上架秒杀商品 保证幂等性 防止重复上架
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);

        try {
            flashSaleService.uploadFlashSaleSkuLatest3Days();
        } finally {
            lock.unlock();
        }


    }

}

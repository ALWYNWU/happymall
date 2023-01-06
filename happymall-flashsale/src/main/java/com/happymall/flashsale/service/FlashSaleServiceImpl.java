package com.happymall.flashsale.service;

import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.happymall.flashsale.feign.CouponFeignService;
import com.happymall.flashsale.vo.SeckillSessionsWithSkus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2023-01-05 11:27 p.m.
 * @description
 */
@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    @Override
    public void uploadFlashSaleSkuLatest3Days() {

        // 扫描最近三天参加秒杀的商品
        R session = couponFeignService.getLatest3DaysSession();
        if (session.getCode() == 0){

            // 上架商品
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });

            // 缓存到redis
            // 1. 秒杀活动信息



        }


    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            List<String> entityIdList =
                    session.getRelationEntities().stream().map(item -> item.getId().toString()).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(key, entityIdList);

        });


    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){}

}

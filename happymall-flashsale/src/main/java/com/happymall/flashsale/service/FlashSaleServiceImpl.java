package com.happymall.flashsale.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.happymall.flashsale.feign.CouponFeignService;
import com.happymall.flashsale.feign.ProductFeignService;
import com.happymall.flashsale.to.SecKillSkuRedisTo;
import com.happymall.flashsale.vo.SeckillSessionsWithSkus;
import com.happymall.flashsale.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2023-01-05 11:27 p.m.
 * @description
 */
@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    CouponFeignService couponFeignService;

    ProductFeignService productFeignService;

    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    public FlashSaleServiceImpl(ProductFeignService productFeignService,
                                CouponFeignService couponFeignService,
                                RedissonClient redissonClient){
        this.productFeignService = productFeignService;
        this.couponFeignService = couponFeignService;
        this.redissonClient = redissonClient;
    }


    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOKE_SEMAPHORE = "seckill:stoke"; // +商品随机码

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
            saveSessionInfos(sessionData);

            // 2. 缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);

        }


    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;

            // 判断redis中是否已经有这个商品
            Boolean hasKey = redisTemplate.hasKey(key);

            if (!hasKey){
                // session使用list储存该场次的商品
                List<String> entityIdList =
                        session.getRelationEntities().stream()
                                .map(item -> item.getPromotionSessionId().toString()+"_"+item.getSkuId().toString()).collect(Collectors.toList());

                // 缓存秒杀活动信息
                redisTemplate.opsForList().leftPushAll(key, entityIdList);
            }

        });


    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.forEach(session -> {

            // 准备redis hash操作
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationEntities().stream().forEach(seckillSkuVo -> {

                // 4. 随机码 防止恶意攻击 每一个商品需要设置随机码
                String token = UUID.randomUUID().toString().replace("-", "");

                if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString())){
                    //缓存商品
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                    // 1. Sku 基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0){
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(info);
                    }

                    // 2. sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    // 3. 设置商品时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());


                    redisTo.setRandomCode(token);

                    String s = JSON.toJSONString(redisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString(), s);

                    // 5. 使用库存作为分布式信号量 信号量用来限流
                    // 秒杀开始之后 每进来一个请求 信号量就会减一
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOKE_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }

            });


        });

    }

}

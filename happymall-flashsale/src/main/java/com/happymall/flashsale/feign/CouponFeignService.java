package com.happymall.flashsale.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yilong
 * @date 2023-01-05 11:30 p.m.
 * @description
 */

@FeignClient("happymall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/latest3DaysSession")
    R getLatest3DaysSession();
}

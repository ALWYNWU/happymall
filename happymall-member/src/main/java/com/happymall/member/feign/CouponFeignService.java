package com.happymall.member.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yilong
 * @date 2022-09-05 7:01 p.m.
 * @description
 */

@FeignClient("happymall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();

}

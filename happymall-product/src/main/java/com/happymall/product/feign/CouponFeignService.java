package com.happymall.product.feign;

import com.common.to.SkuReductionTo;
import com.common.to.SpuBoundsTo;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Yilong
 * @date 2022-10-06 5:48 p.m.
 * @description
 */

@FeignClient("happymall-coupon")
public interface CouponFeignService {

    /**
     * 1. CouponFeignService.saveSpuBounds(spuBoundsTo)
     *    1.1  @RequestBody will convert this object to JSON
     *    1.2  Send request to /coupon/spubounds/save (contain JSON data)
     *    1.3  Receiver receive the request and convert json to specific object
     * @param spuBoundsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}

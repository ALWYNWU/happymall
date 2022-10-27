package com.happymall.ware.feign;

import com.common.to.SkuReductionTo;
import com.common.to.SpuBoundsTo;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yilong
 * @date 2022-10-07 11:43 p.m.
 * @description
 */
@FeignClient("happymall-product")
public interface ProductFeignService {

    /**
     * 1. CouponFeignService.saveSpuBounds(spuBoundsTo)
     *    1.1  @RequestBody will convert this object to JSON
     *    1.2  Send request to /coupon/spubounds/save (contain JSON data)
     *    1.3  Receiver receive the request and convert json to specific object
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R findSkuName(@PathVariable("skuId") Long skuId);


}

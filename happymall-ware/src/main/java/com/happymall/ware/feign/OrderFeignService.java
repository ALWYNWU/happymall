package com.happymall.ware.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yilong
 * @date 2022-10-24 9:22 p.m.
 * @description
 */

@FeignClient("happymall-order")
public interface OrderFeignService {

    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);

}

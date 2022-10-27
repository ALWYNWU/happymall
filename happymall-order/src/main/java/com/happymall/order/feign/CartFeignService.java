package com.happymall.order.feign;

import com.happymall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-23 12:38 a.m.
 * @description
 */
@FeignClient("happymall-cart")
public interface  CartFeignService {

    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();


}

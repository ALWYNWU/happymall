package com.happymall.member.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Yilong
 * @date 2022-10-25 4:34 p.m.
 * @description
 */
@FeignClient("happymall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);

}

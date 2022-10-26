package com.happymall.product.feign;

import com.common.to.SkuHasStockVo;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-10 1:04 p.m.
 * @description
 */
@FeignClient("happymall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);

    @PostMapping("/ware/waresku/stock")
    Boolean getSkuHasStock(@RequestParam Long skuId);


}

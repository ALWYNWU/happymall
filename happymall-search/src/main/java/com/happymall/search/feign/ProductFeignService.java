package com.happymall.search.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-14 11:56 p.m.
 * @description
 */

@FeignClient("happymall-product")
public interface  ProductFeignService {

    @GetMapping("product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    R infos(@RequestParam("brandIds") List<Long> brandIds);

    @GetMapping("/product/category/info/{catId}")
    R cateInfo(@PathVariable("catId") Long catId);

    @RequestMapping("/product/skuinfo/info/{skuId}/price")
    R price(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/price")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);


/*    @GetMapping("/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        return skuInfoService.getById(skuId).getPrice();
    }*/

}

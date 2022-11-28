package com.happymall.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.happymall.product.entity.SkuInfoEntity;
import com.happymall.product.service.SkuInfoService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * sku信息
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    @RequestMapping("/info/{skuId}/price")
    public R price(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return R.ok().put("skuPrice", price);
    }

    @GetMapping("/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        return skuInfoService.getById(skuId).getPrice();
    }


    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }


    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }


    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}

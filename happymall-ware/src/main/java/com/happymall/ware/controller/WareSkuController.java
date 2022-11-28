package com.happymall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.common.exception.BizCodeEnume;
import com.happymall.ware.exception.NoStockException;
import com.happymall.ware.vo.LockStockResult;
import com.happymall.ware.vo.SkuHasStockVo;
import com.happymall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.happymall.ware.entity.WareSkuEntity;
import com.happymall.ware.service.WareSkuService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){

        try {
            Boolean stockResults = wareSkuService.orderLockStock(vo);
            return R.ok();
        } catch (NoStockException e){
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(), BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }


    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> vos =  wareSkuService.getSkusHasStock(skuIds);

        R ok = R.ok();
        ok.setData(vos);
        return ok;
    }

    // Query sku stock
    @PostMapping("/stock")
    public Boolean getSkuHasStock(@RequestParam Long skuId){
        return wareSkuService.getSkuHasStock(skuId);
    }


    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }


    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }


    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }


    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

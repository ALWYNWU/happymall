package com.happymall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.happymall.ware.vo.MergeVo;
import com.happymall.ware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.happymall.ware.entity.PurchaseEntity;
import com.happymall.ware.service.PurchaseService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 采购信息
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/done")
    public R finish(@RequestBody PurchaseFinishVo doneVo){
        purchaseService.done(doneVo);

        return R.ok();
    }



    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);

        return R.ok();
    }


    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){

        purchaseService.mergePurchase(mergeVo);

        return R.ok();
    }


    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

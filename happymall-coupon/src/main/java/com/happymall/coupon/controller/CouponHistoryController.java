package com.happymall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.happymall.coupon.entity.CouponHistoryEntity;
import com.happymall.coupon.service.CouponHistoryService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 优惠券领取历史记录
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-08-18 00:48:15
 */
@RestController
@RequestMapping("coupon/couponhistory")
public class CouponHistoryController {
    @Autowired
    private CouponHistoryService couponHistoryService;


    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CouponHistoryEntity couponHistory = couponHistoryService.getById(id);

        return R.ok().put("couponHistory", couponHistory);
    }


    @RequestMapping("/save")
    public R save(@RequestBody CouponHistoryEntity couponHistory){
		couponHistoryService.save(couponHistory);

        return R.ok();
    }


    @RequestMapping("/update")
    public R update(@RequestBody CouponHistoryEntity couponHistory){
		couponHistoryService.updateById(couponHistory);

        return R.ok();
    }


    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		couponHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

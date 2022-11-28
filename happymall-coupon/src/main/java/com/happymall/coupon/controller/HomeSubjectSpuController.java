package com.happymall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.happymall.coupon.entity.HomeSubjectSpuEntity;
import com.happymall.coupon.service.HomeSubjectSpuService;
import com.common.utils.PageUtils;
import com.common.utils.R;

/**
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-08-18 00:48:15
 */
@RestController
@RequestMapping("coupon/homesubjectspu")
public class HomeSubjectSpuController {
    @Autowired
    private HomeSubjectSpuService homeSubjectSpuService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = homeSubjectSpuService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		HomeSubjectSpuEntity homeSubjectSpu = homeSubjectSpuService.getById(id);

        return R.ok().put("homeSubjectSpu", homeSubjectSpu);
    }


    @RequestMapping("/save")
    public R save(@RequestBody HomeSubjectSpuEntity homeSubjectSpu){
		homeSubjectSpuService.save(homeSubjectSpu);

        return R.ok();
    }


    @RequestMapping("/update")
    public R update(@RequestBody HomeSubjectSpuEntity homeSubjectSpu){
		homeSubjectSpuService.updateById(homeSubjectSpu);

        return R.ok();
    }


    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		homeSubjectSpuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

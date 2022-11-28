package com.happymall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.common.validator.group.AddGroup;
import com.common.validator.group.UpdateGroup;
import com.common.validator.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.happymall.product.entity.BrandEntity;
import com.happymall.product.service.BrandService;
import com.common.utils.PageUtils;
import com.common.utils.R;

import javax.management.remote.rmi._RMIConnection_Stub;


/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    @GetMapping("/infos")
    public R infos(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brandEntities = brandService.getBrandsByIds(brandIds);

        return R.ok().put("brand", brandEntities);
    }

    /**
     * Save
     * @Validated Cope with complex verification in multiple scenarios
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * Update brand and associated data
     */
    @Transactional
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetails(brand);

        return R.ok();
    }

    /**
     * Update status
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}

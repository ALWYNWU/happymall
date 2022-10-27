package com.happymall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.happymall.product.entity.ProductAttrValueEntity;
import com.happymall.product.service.ProductAttrValueService;
import com.happymall.product.vo.AttrResponseVo;
import com.happymall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.happymall.product.service.AttrService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 商品属性
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){

        List<ProductAttrValueEntity> entities =  productAttrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data", entities);
    }

    /**
     * product/attr/base/list/{catelogId}
     * Get attribute list
     * @param params used for fuzzy query
     * @param catelogId
     * @return
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type){

        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);

        return R.ok().put("page", page);
    }




    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * Get attribute info
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrResponseVo responseVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", responseVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId")  Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSupAttr(spuId, entities);

        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

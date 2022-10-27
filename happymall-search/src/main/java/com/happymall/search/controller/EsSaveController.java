package com.happymall.search.controller;

import com.common.exception.BizCodeEnume;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.happymall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-10 2:18 p.m.
 * @description
 */

@Slf4j
@RequestMapping("/search/save")
@RestController
public class EsSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        boolean productSaveStatus = false;

        try {
            productSaveStatus = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e){
            log.error("ElasticSaveController ERROR: {}", e);
            return R.error(BizCodeEnume.PRODUCT_ACTIVATE_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_ACTIVATE_EXCEPTION.getMsg());

        }

        if(!productSaveStatus){
            return R.ok();
        } else {
            return R.error(BizCodeEnume.PRODUCT_ACTIVATE_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_ACTIVATE_EXCEPTION.getMsg());
        }



    }

}

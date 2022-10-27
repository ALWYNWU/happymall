package com.happymall.product.web;

import com.happymall.product.service.SkuInfoService;
import com.happymall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author Yilong
 * @date 2022-10-15 6:43 p.m.
 * @description
 */

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        System.out.println("Query:" + skuId + " detail");
        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);

        return "item";
    }

}

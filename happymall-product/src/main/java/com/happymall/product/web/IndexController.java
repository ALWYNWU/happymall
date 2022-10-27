package com.happymall.product.web;

import com.happymall.product.entity.CategoryEntity;
import com.happymall.product.service.CategoryService;
import com.happymall.product.vo.CatelogTwoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Yilong
 * @date 2022-10-10 10:07 p.m.
 * @description
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){

        List<CategoryEntity> categoryEntities =  categoryService.getLevelOneCategories();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<CatelogTwoVo>> getCatalogJson(){

        Map<String, List<CatelogTwoVo>> categoryJson = categoryService.getCatalogJson();
        return categoryJson;
    }

}

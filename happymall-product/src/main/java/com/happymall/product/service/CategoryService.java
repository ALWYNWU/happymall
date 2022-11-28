package com.happymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.happymall.product.entity.CategoryEntity;
import com.happymall.product.vo.CatelogTwoVo;

import java.util.List;
import java.util.Map;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-07 18:10:11
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeCategoryByIds(List<Long> asList);

    /**
     * find catelogId complete path
     * [parent/children/..]
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevelOneCategories();

    Map<String, List<CatelogTwoVo>> getCatalogJson();
}


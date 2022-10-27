package com.happymall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.happymall.product.service.CategoryBrandRelationService;
import com.happymall.product.vo.CatelogTwoVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.product.dao.CategoryDao;
import com.happymall.product.entity.CategoryEntity;
import com.happymall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        // 1. Get all categories
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 2. assemble into tree structure
        // Use stream api generate an element sequence and filter them, then collect as list
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((category) -> {
            category.setChildren(getChildren(category, entities));
            return category;
        }).sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort()))).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeCategoryByIds(List<Long> asList) {

        //TODO Checks whether the currently deleted category is referenced elsewhere

        // In project we usually use logical deletion
        baseMapper.deleteBatchIds(asList);
    }

    // [2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        paths.add(catelogId);
        CategoryEntity entity = this.getById(catelogId);

        if(entity.getParentCid()!=0){
            findParentPath(entity.getParentCid(), paths);
        }
        return paths;

    }

    /**
     * Update all associated data
     * @CacheEvict: Clear cache after update
     * @param: Updated category
     */

    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        // Update it self
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    @Cacheable(value = "category", key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevelOneCategories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    // TODO OutOfDirectMemoryError
    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<CatelogTwoVo>> getCatalogJson(){

        /**
         * Optimize Query database on time
         */
        List<CategoryEntity> allCategoriesList = baseMapper.selectList(null);

        // Get level one category
        List<CategoryEntity> levelOneCategories = getChildCategoriesByParentId(allCategoriesList,0L);

        // Encapsulate data
        Map<String, List<CatelogTwoVo>> categoryMap = levelOneCategories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            // 1. Every level one category, get it's level two categories
            List<CategoryEntity> categoryEntities = getChildCategoriesByParentId(allCategoriesList,v.getCatId());

            // 2. Encapsulate entities to view object
            List<CatelogTwoVo> catelogTwoVos = null;
            if (categoryEntities != null) {
                catelogTwoVos = categoryEntities.stream().map(l2 -> {
                    CatelogTwoVo catelogTwoVo = new CatelogTwoVo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    List<CategoryEntity> catelogThreeEntities =  getChildCategoriesByParentId(allCategoriesList, l2.getCatId());
                    if (catelogThreeEntities != null){
                        List<CatelogTwoVo.Catelog3Vo> catelogThreeVos = catelogThreeEntities.stream().map(l3 -> {
                            CatelogTwoVo.Catelog3Vo catelog3Vo = new CatelogTwoVo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelogTwoVo.setCatalog3List(catelogThreeVos);

                    }

                    return catelogTwoVo;
                }).collect(Collectors.toList());
            }


            return catelogTwoVos;
        }));
        return categoryMap;
    }

/*    // Get Data from Database
    public Map<String, List<CatelogTwoVo>> getCatalogJsonFromDbWithRedissonLock() {
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<CatelogTwoVo>> dataFromDb;

        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    // Get Data from Database
    public Map<String, List<CatelogTwoVo>> getCatalogJsonFromDb() {

        synchronized (this){

            // Check cache again
            return getDataFromDb();
        }


    }*/

    private Map<String, List<CatelogTwoVo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(!StringUtils.isEmpty(catalogJSON)){
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<CatelogTwoVo>>>() {});
        }

        /**
         * Optimize Query database on time
         */
        List<CategoryEntity> allCategoriesList = baseMapper.selectList(null);


        // Get level one category
        List<CategoryEntity> levelOneCategories = getChildCategoriesByParentId(allCategoriesList,0L);

        // Encapsulate data
        Map<String, List<CatelogTwoVo>> categoryMap = levelOneCategories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            // 1. Every level one category, get it's level two categories
            List<CategoryEntity> categoryEntities = getChildCategoriesByParentId(allCategoriesList,v.getCatId());

            // 2. Encapsulate entities to view object
            List<CatelogTwoVo> catelogTwoVos = null;
            if (categoryEntities != null) {
                catelogTwoVos = categoryEntities.stream().map(l2 -> {
                    CatelogTwoVo catelogTwoVo = new CatelogTwoVo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    List<CategoryEntity> catelogThreeEntities =  getChildCategoriesByParentId(allCategoriesList, l2.getCatId());
                    if (catelogThreeEntities != null){
                        List<CatelogTwoVo.Catelog3Vo> catelogThreeVos = catelogThreeEntities.stream().map(l3 -> {
                            CatelogTwoVo.Catelog3Vo catelog3Vo = new CatelogTwoVo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelogTwoVo.setCatalog3List(catelogThreeVos);

                    }

                    return catelogTwoVo;
                }).collect(Collectors.toList());
            }


            return catelogTwoVos;
        }));

        String s = JSON.toJSONString(categoryMap);
        redisTemplate.opsForValue().set("catalogJSON",s,1, TimeUnit.HOURS);

        return categoryMap;
    }

    private List<CategoryEntity> getChildCategoriesByParentId(List<CategoryEntity> levelOneCategories, Long parentId) {
        return levelOneCategories.stream().filter(item -> item.getParentCid().equals(parentId)).collect(Collectors.toList());
    }

    // Recursively find sub categories of categories
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            // Find subcategory
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort()))).collect(Collectors.toList());

        return children;
    }

}
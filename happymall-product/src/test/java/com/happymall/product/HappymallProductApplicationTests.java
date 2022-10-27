package com.happymall.product;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.happymall.product.dao.SkuSaleAttrValueDao;
import com.happymall.product.entity.BrandEntity;
import com.happymall.product.service.AttrGroupService;
import com.happymall.product.service.BrandService;
import com.happymall.product.service.CategoryService;
import com.happymall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HappymallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void testItem(){
//        List<SkuItemVo.SpuItemAttrGroupVo> attrGroupWithAttrsBySkuId = attrGroupService.getAttrGroupWithAttrsBySpuId(7L, 225L);
//        System.out.println(attrGroupWithAttrsBySkuId);
        List<SkuItemVo.SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(8L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set("hello", "world_" + UUID.randomUUID().toString());
        System.out.println(ops.get("hello"));

    }

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("Complete path: {}", Arrays.asList(catelogPath));
    }

    @Test
    public void contextLoads() {

        /* BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("apple yyds");
        brandService.updateById(brandEntity);

        brandEntity.setName("oppo");
        brandService.save(brandEntity);
        System.out.println("Success!");*/


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));
        list.forEach((item) -> {
            System.out.println(item);
        });

    }

}

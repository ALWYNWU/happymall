package com.happymall.product.vo;

import com.happymall.product.entity.SkuImagesEntity;
import com.happymall.product.entity.SkuInfoEntity;
import com.happymall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-15 6:54 p.m.
 * @description
 */
@Data
public class SkuItemVo {

    // 1. sku basic info, title, price etc
    SkuInfoEntity info;

    boolean hasStock = false;

    // 2. sku image info
    List<SkuImagesEntity> images;

    // 3. Sale attributes(color, storage etc)
    List<SkuItemSaleAttrVo> saleAttrs;

    // 4. spu description
    SpuInfoDescEntity description;

    // 5. spu specifications
    List<SpuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }

    @Data
    public static class AttrValueWithSkuIdVo {
        private String attrValue;
        private String skuIds;
    }


    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrValues;
    }

    @Data
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }

}

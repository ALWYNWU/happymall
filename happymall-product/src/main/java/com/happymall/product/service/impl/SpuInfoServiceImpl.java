package com.happymall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.common.constant.ProductConstant;
import com.common.to.SkuHasStockVo;
import com.common.to.SkuReductionTo;
import com.common.to.SpuBoundsTo;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.happymall.product.entity.*;
import com.happymall.product.feign.CouponFeignService;
import com.happymall.product.feign.SearchFeignService;
import com.happymall.product.feign.WareFeignService;
import com.happymall.product.service.*;
import com.happymall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        // Save basic information 'pms_spu_info'
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);


        // Save spu description images 'pms_spu_info_desc'\
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);


        // Save spu image collection 'pms_spu_images'
        List<String> images = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(), images);


        // Save spu specifications and attributes 'pms_product_attr_value'
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            valueEntity.setAttrName(attrService.getById(attr.getAttrId()).getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);


        // Save spu points information 'happymall_sms -> sms_spu_bounds'
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);

        if(r.getCode() != 0){
            log.error("Remote call save SKU points failed!!");
        }

        // Save sku information
        // a. save sku basic information 'pms_sku_info'
        List<Skus> skus = vo.getSkus();
        if(skus != null && skus.size() > 0){
            skus.forEach(sku -> {

                String defaultImg = " ";
                for(Images image : sku.getImages()){
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    // filter: return false will be filtered
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                // b. save sku images information 'pms_sku_images'
                skuImagesService.saveBatch(imagesEntities);
                //TODO Do not save image that without path

                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> attrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());

                // c. save sku sale attributes 'pms_sku_sale_attr_value'
                skuSaleAttrValueService.saveBatch(attrValueEntities);

                // d. save sku discount info
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);

                if(skuReductionTo.getDiscount().compareTo(BigDecimal.ZERO) == 1 ){
                    BigDecimal bd = skuReductionTo.getDiscount();
                    BigDecimal realDiscount = (new BigDecimal("100").subtract(bd)).divide(new BigDecimal("100"));
                    skuReductionTo.setDiscount(realDiscount);
                }



                skuReductionTo.setSkuId(skuId);

                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);

                    if(r1.getCode() != 0){
                        log.error("Remote call save SKU discount information failed!!");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key =(String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(con -> {
                con.eq("id", key).or().like("spu_name", key);
            });
        }

        String status =(String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);

        }

        String brandId =(String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId =(String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        // 1. Query all sku info corresponding to the spuId
        List<SkuInfoEntity> skuInfoEntities =  skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 2. Query attributes of current sku that can be used to be index
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        // Get all attributes id
        List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

        // Select corresponding attribute entities' id which search type equal 1
        List<Long> indexedAttrIds =  attrService.selectIndexedAttrs(attrIds);
        // Convert it to hashSet cause it is easy to check if it contains indexedAttr's id
        Set<Long> idSet = new HashSet<>(indexedAttrIds);

        // filter entities, if these attributes can be indexed, copy their fields to esModel.attrs, then set it
        // into SkuEsModel
        List<SkuEsModel.Attrs> esAttrsList = baseAttrs.stream().filter(attr -> {
            return idSet.contains(attr.getAttrId());
        }).map(attr -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attr, attrs);
            return attrs;
        }).collect(Collectors.toList());

        // hasStock
        // TODO RPC query stock (ware service)
        Map<Long, Boolean> hasStockMap = null;
        try {
            R skusHasStock = wareFeignService.getSkusHasStock(skuIdList);

            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>(){};

            hasStockMap = skusHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e){
            log.error("Ware service query error:  {}", e);
        }



        // 2.Encapsulate each sku info
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModel> upProducts = skuInfoEntities.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);

            // skuPrice <==> Price
            esModel.setSkuPrice(sku.getPrice());

            // SkuImg <==> SkuDefaultImg
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // Set stock info
            if(finalHasStockMap == null){
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalHasStockMap.get(sku.getSkuId()));
            }

            // TODO hot score 0
            esModel.setHotScore(0L);

            // TODO query brand name and category name
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            // Set index attributes
            esModel.setAttrs(esAttrsList);


            return esModel;
        }).collect(Collectors.toList());

        // TODO send to es
        R productActivateResponse = searchFeignService.productStatusUp(upProducts);
        if (productActivateResponse.getCode() == 0){
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_AVAILABLE.getCode());
        } else {
            // TODO 接口幂等性 同一个接口，多次发出同一个请求，必须保证操作只执行一次。

            /**
             * Idempotent means that you can apply the operation a number of times, but the resulting state of one call
             * will be indistinguishable from the resulting state of multiple calls. In short, it is safe to call the
             * method multiple times. Effectively the second and third (and so on) calls will have no visible effect on
             * the state of the program.
             */
        }

    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        return getById(skuInfoEntity.getSpuId());
    }

}
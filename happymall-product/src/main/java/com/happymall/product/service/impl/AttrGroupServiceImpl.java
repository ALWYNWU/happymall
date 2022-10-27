package com.happymall.product.service.impl;

import com.happymall.product.entity.AttrEntity;
import com.happymall.product.entity.ProductAttrValueEntity;
import com.happymall.product.service.AttrService;
import com.happymall.product.service.ProductAttrValueService;
import com.happymall.product.vo.AttrGroupWithAttrsVo;
import com.happymall.product.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.product.dao.AttrGroupDao;
import com.happymall.product.entity.AttrGroupEntity;
import com.happymall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        // select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }

        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        // 1. Get group info
        List<AttrGroupEntity> groupEntities =
                this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        // 2. Get attributes
        List<AttrGroupWithAttrsVo> collect = groupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttrs(item.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrs);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        AtomicBoolean validSpuId = new AtomicBoolean(true);

        // Get group list with attrs
        List<AttrGroupWithAttrsVo> attrGroupList = this.getAttrGroupWithAttrsByCatelogId(catalogId);

        // create return groupVoList
        List<SkuItemVo.SpuItemAttrGroupVo> groupVoList = new ArrayList<>();

        // traverse every group
        for (AttrGroupWithAttrsVo groupWithAttrs : attrGroupList) {

            // Set attribute group name
            SkuItemVo.SpuItemAttrGroupVo spuItemAttrGroupVo = new SkuItemVo.SpuItemAttrGroupVo();
            spuItemAttrGroupVo.setGroupName(groupWithAttrs.getAttrGroupName());

            // Get attributes from group
            List<AttrEntity> attrList = groupWithAttrs.getAttrs();

            // traverse the attribute list and get attribute name and value corresponding to spuId
            List<SkuItemVo.SpuBaseAttrVo> attrValues = attrList.stream().map(item -> {

                // Set attribute name
                SkuItemVo.SpuBaseAttrVo spuBaseAttrVo = new SkuItemVo.SpuBaseAttrVo();
                spuBaseAttrVo.setAttrName(item.getAttrName());

                // Get attribute id
                Long id = item.getAttrId();

                // Get attribute value by spuId and attribute id, because attribute value may have multiple value
                ProductAttrValueEntity attrValueEntity = productAttrValueService.getOne(
                        new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).eq("attr_id", id));

                // Set attribute value
                if (attrValueEntity != null){
                    spuBaseAttrVo.setAttrValue(attrValueEntity.getAttrValue());
                } else {
                    validSpuId.set(false);
                }


                // Return and collect as list
                return spuBaseAttrVo;

            }).collect(Collectors.toList());

            if (validSpuId.get()){
                spuItemAttrGroupVo.setAttrValues(attrValues);
            } else {
                return new ArrayList<>();
            }



            groupVoList.add(spuItemAttrGroupVo);
        }


/*        @Data
        public static class SpuItemAttrGroupVo {
            private String groupName;
            private List<SkuItemVo.SpuBaseAttrVo> attrValues;
        }

        @Data
        public static class SpuBaseAttrVo{
            private String attrName;
            private String attrValue;
        }*/

/*        public class AttrGroupWithAttrsVo {
            private Long attrGroupId;
            private String attrGroupName;
            private Integer sort;
            private String descript;
            private String icon;
            private Long catelogId;
            private List<AttrEntity> attrs;
        }*/

       /* public class AttrEntity implements Serializable {
            private Long attrId;
            private String attrName;*/


        return groupVoList;
    }

}
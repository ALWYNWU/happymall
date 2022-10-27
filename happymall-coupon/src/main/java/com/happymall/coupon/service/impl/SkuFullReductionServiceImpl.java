package com.happymall.coupon.service.impl;

import com.common.to.MemberPrice;
import com.common.to.SkuReductionTo;
import com.happymall.coupon.entity.MemberPriceEntity;
import com.happymall.coupon.entity.SkuLadderEntity;
import com.happymall.coupon.service.MemberPriceService;
import com.happymall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.coupon.dao.SkuFullReductionDao;
import com.happymall.coupon.entity.SkuFullReductionEntity;
import com.happymall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 1. save sales info (Ex: buy two get 20% off)
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());

        if(skuReductionTo.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }

        // 2. save reduction info (Ex: 100 - 20, 200 - 50)
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, reductionEntity);

        if(reductionEntity.getFullPrice().compareTo(BigDecimal.ZERO) == 1 ){
            this.save(reductionEntity);
        }

        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(price -> {
            MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(skuReductionTo.getSkuId());
            priceEntity.setMemberLevelId(price.getId());
            priceEntity.setMemberPrice(price.getPrice());
            priceEntity.setAddOther(1);
            return priceEntity;
        }).filter(item -> {
            return item.getMemberPrice().compareTo(BigDecimal.ZERO) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}
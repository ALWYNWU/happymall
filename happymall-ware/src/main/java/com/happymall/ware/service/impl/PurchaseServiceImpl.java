package com.happymall.ware.service.impl;

import com.common.constant.WareConstant;
import com.happymall.ware.service.WareSkuService;
import com.happymall.ware.vo.MergeVo;
import com.happymall.ware.entity.PurchaseDetailEntity;
import com.happymall.ware.service.PurchaseDetailService;
import com.happymall.ware.vo.PurchaseFinishVo;
import com.happymall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.ware.dao.PurchaseDao;
import com.happymall.ware.entity.PurchaseEntity;
import com.happymall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchasedId = mergeVo.getPurchaseId();
        if(purchasedId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchasedId = purchaseEntity.getId();

        }

        List<Long> items = mergeVo.getItems();

        Long finalPurchasedId = purchasedId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchasedId);
            detailEntity.setStatus(WareConstant.PurchaseDetailsStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchasedId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity entities = this.getById(id);
            return entities;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);

        collect.forEach(item -> {
            List<PurchaseDetailEntity> entities =  purchaseDetailService.listDetailByPurchasedId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map(e -> {
                PurchaseDetailEntity ent = new PurchaseDetailEntity();
                ent.setId(e.getId());
                ent.setStatus(WareConstant.PurchaseDetailsStatusEnum.BUYING.getCode());
                return ent;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseFinishVo doneVo) {

        // 1. Change purchase list status
        Long id = doneVo.getId();

        // 2. Change purchase item status
        boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for(PurchaseItemDoneVo item : items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(item.getStatus() == WareConstant.PurchaseStatusEnum.HASERROR.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else{
                detailEntity.setStatus(WareConstant.PurchaseDetailsStatusEnum.FINISHED.getCode());

                // 3. Change products stock
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());

            }

            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);


        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus
                (flag ? WareConstant.PurchaseStatusEnum.FINISHED.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);



    }

}
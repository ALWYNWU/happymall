package com.happymall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.common.to.OrderTo;
import com.common.to.mq.StockDetailTo;
import com.common.to.mq.StockLockedTo;
import com.common.utils.R;
import com.happymall.ware.entity.WareOrderTaskDetailEntity;
import com.happymall.ware.entity.WareOrderTaskEntity;
import com.happymall.ware.exception.NoStockException;
import com.happymall.ware.feign.OrderFeignService;
import com.happymall.ware.feign.ProductFeignService;
import com.happymall.ware.service.WareOrderTaskDetailService;
import com.happymall.ware.service.WareOrderTaskService;
import com.happymall.ware.vo.*;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.ware.dao.WareSkuDao;
import com.happymall.ware.entity.WareSkuEntity;
import com.happymall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService orderTaskService;

    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId){
        wareSkuDao.unlockStock(skuId,wareId,num);
        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(taskDetailId);
        detailEntity.setLockStatus(2);
        orderTaskDetailService.updateById(detailEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        List<WareSkuEntity> entities = wareSkuDao.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if(entities.size()==0 || entities == null){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try {
                R skuName = productFeignService.findSkuName(skuId);
                HashMap<String, String> map = (HashMap<String, String>) skuName.get("skuInfo");

                if(skuName.getCode()== 0 ){
                    wareSkuEntity.setSkuName(map.get("skuName"));
                }
            } catch (Exception e){
            }

            wareSkuDao.insert(wareSkuEntity);
        }

        wareSkuDao.addStock(skuId, wareId, skuNum);

    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Data

    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }


    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {

        // Warehouse order task info
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);


        // According to delivery address, find the nearest warehouse
        // Find which warehouse has enough stock for current item
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());

            // Query which warehouse has stock
            List<Long> wareIds =  wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareIds(wareIds);
            return stock;
        }).collect(Collectors.toList());


        // Lock stock
        for (SkuWareHasStock item : collect) {
            Boolean skuStocked = false;
            Long skuId = item.getSkuId();
            List<Long> wareIds = item.getWareIds();

            if (wareIds == null || wareIds.size() == 0){
                // This item is out of stock
                throw new NoStockException(skuId);
            }

            // IF every item lock stock successfully, send lock details to MQ
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, item.getNum());
                if (count == 1){
                    skuStocked = true;

                    // Generate warehouse order task detail entity
                    WareOrderTaskDetailEntity detailEntity =
                            new WareOrderTaskDetailEntity(null, skuId, "", item.getNum(), taskEntity.getId(), wareId, 1);
                    orderTaskDetailService.save(detailEntity);

                    // Transform object of MQ
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());

                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity, detailTo);
                    lockedTo.setDetailTo(detailTo);

                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;
                } else {
                    // Current warehouse do not have enough stock, try next warehouse
                }
            }

            if (skuStocked == false){
                throw new NoStockException(skuId);
            }

        }

        return true;
    }



    @Override
    public Boolean getSkuHasStock(Long skuId) {
        WareSkuEntity entity = wareSkuDao.selectOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
        if (entity != null){
            return (entity.getStock() > 0);
        } else {
            return false;
        }
    }

    @Override
    public void unlockStock(StockLockedTo to) {


        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();

        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if (byId != null){
            // Means lock stock successful
            // 1. The order doesn't exist, release stock
            // 2. Order exist, status: cancel or payment failed, release stock
            //                 status: normal, do not release stock

            Long id = to.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0){
                OrderVo data = r.getData(new TypeReference<OrderVo>() {});

                // This order have been canceled
                if (data == null || data.getStatus() == 4){
                    if (byId.getLockStatus()==1){
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }

                }
            } else {
                // Reject message and put it into queue again
                throw new RuntimeException("RPC fail");
            }

        } else {
            // Do not need release stock
        }


    }

    @Override
    @Transactional
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();

        // Check stock lock status, prevent release stock repeatedly
        WareOrderTaskEntity task =
                orderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));
        Long id = task.getId();
        // Get all unlock stock item
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id)
                .eq("lock_status", 1));

        for (WareOrderTaskDetailEntity entity : entities) {
            unLockStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum(), entity.getId());
        }

    }

}
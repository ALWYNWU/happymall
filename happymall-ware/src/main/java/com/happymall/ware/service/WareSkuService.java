package com.happymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.to.OrderTo;
import com.common.to.mq.StockLockedTo;
import com.common.utils.PageUtils;
import com.happymall.ware.entity.WareSkuEntity;
import com.happymall.ware.vo.LockStockResult;
import com.happymall.ware.vo.SkuHasStockVo;
import com.happymall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    Boolean getSkuHasStock(Long skuId);

    void unlockStock(StockLockedTo to);

    void unlockStock(OrderTo orderTo);

}


package com.happymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.happymall.ware.vo.MergeVo;
import com.happymall.ware.entity.PurchaseEntity;
import com.happymall.ware.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseFinishVo doneVo);
}


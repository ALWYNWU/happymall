package com.happymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.happymall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 15:02:21
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


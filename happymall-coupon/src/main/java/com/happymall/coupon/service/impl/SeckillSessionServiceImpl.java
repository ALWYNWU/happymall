package com.happymall.coupon.service.impl;

import com.happymall.coupon.entity.SeckillSkuRelationEntity;
import com.happymall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.coupon.dao.SeckillSessionDao;
import com.happymall.coupon.entity.SeckillSessionEntity;
import com.happymall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    private SeckillSkuRelationService seckillSkuRelationService;

    public SeckillSessionServiceImpl (SeckillSkuRelationService seckillSkuRelationService){
        this.seckillSkuRelationService = seckillSkuRelationService;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaysSession() {

        // 计算最近三天的日期
        List<SeckillSessionEntity> list =
                this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));

        if (list != null && list.size() > 0){
            return list.stream().map(session -> {
                Long id = session.getId();
                List<SeckillSkuRelationEntity> entities =
                        seckillSkuRelationService.list(
                                new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                session.setRelationEntities(entities);
                return session;
            }).collect(Collectors.toList());

        }

        return null;
    }

    private String startTime(){
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String endTime(){
        return LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
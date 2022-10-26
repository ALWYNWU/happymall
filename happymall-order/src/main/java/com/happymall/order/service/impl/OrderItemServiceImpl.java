package com.happymall.order.service.impl;

import com.happymall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.order.dao.OrderItemDao;
import com.happymall.order.entity.OrderItemEntity;
import com.happymall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * Receive message
     * @param message
     * @param content
     * @param channel
     */
    @RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel){
        System.out.println("Receive message: "+message+"=======> Content: " + content);
    }

}
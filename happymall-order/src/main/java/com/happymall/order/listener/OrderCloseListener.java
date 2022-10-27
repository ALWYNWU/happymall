package com.happymall.order.listener;

import com.happymall.order.entity.OrderEntity;
import com.happymall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;

/**
 * @author Yilong
 * @date 2022-10-24 11:02 p.m.
 * @description
 */
@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("RECEIVE EXPIRE ORDER MESSAGE, PREPARE CLOSE ORDER!!!" + order);

        try {
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}

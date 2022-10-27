package com.happymall.ware.listener;
import com.common.to.OrderTo;
import com.common.to.mq.StockLockedTo;
import com.happymall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @author Yilong
 * @date 2022-10-24 10:02 p.m.
 * @description
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 1. lock stock successfully, order has problem and roll back, we need release stock
     * 2. lock stock failed, do not need release stock
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("Receive Release Message");
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo , Message message, Channel channel) throws IOException {
        System.out.println("ORDER CLOSED, PREPARE RELEASE STOCK");
        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }



}

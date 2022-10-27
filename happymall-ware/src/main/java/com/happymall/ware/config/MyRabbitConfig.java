package com.happymall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author Yilong
 * @date 2022-10-24 6:28 p.m.
 * @description
 */

@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue", true, false,false);
    }

    @Bean
    public Queue stockDelayQueue(){

        HashMap<String, Object> arguments = new HashMap<>();
        // Dead letter exchange
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        // Dead letter routing key
        arguments.put("x-dead-letter-routing-key", "stock.release");
        // Queue time to live(TTL)
        arguments.put("x-message-ttl", 120000);


        return new Queue("stock.delay.queue", true, false,false, arguments);
    }

    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#", null);
    }

    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked", null);
    }

}

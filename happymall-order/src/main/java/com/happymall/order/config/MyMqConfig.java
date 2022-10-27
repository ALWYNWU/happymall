package com.happymall.order.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;

/**
 * @author Yilong
 * @date 2022-10-24 4:47 p.m.
 * @description
 */

@Configuration
public class MyMqConfig {



    @Bean
    public Queue orderDelayQueue(){

        HashMap<String, Object> arguments = new HashMap<>();
        // Dead letter exchange
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        // Dead letter routing key
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        // Queue time to live(TTL)
        arguments.put("x-message-ttl", 60000);

        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseQueue(){
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding(){
    //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order", null);

    }

    /**
     * Order release directly bind to stock release queue
     */
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",null);
     }

}

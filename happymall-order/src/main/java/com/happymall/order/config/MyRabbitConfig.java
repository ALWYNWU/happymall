package com.happymall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Yilong
 * @date 2022-10-21 5:40 p.m.
 * @description
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct // After MyRabbitConfig created, call this method
    public void initRabbitTemplate(){

        // Set confirm callback
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            /**
             * If message arrive broker, ack = true
             * @param correlationData correlation data of current message (unique id of current message)
             * @param ack Whether the message was successfully received
             * @param cause Failure reason
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm....");
            }
        });

        // message arrive queue callback
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * If the message do not arrive specific queue, call this callback method
             * @param message message info which delivery failed
             * @param replyCode
             * @param replyText
             * @param exchange
             * @param routingKey
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail message[]");
            }
        });

    }

}

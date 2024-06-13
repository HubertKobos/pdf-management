package com.example.requestService.config;

import com.example.requestService.enums.RabbitMqQueue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String WATERMARK_QUEUE = "waterMarkQueue";
    @Bean
    public Queue newDocumentQueue(){
        return new Queue(RabbitMqQueue.CREATE_NEW_DOCUMENT_QUEUE.getDescription(), false);
    }
    @Bean
    public Queue documentQueue(){
        return new Queue(RabbitMqQueue.DOCUMENT_QUEUE.getDescription(), false);
    }

    @Bean
    public Queue statusQueue(){
        return new Queue(RabbitMqQueue.WATERMARK_QUEUE.getDescription(), false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}

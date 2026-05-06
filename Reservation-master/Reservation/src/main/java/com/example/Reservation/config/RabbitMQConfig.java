package com.example.Reservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Confirmation constants
    public static final String CONFIRMATION_QUEUE = "confirmation.queue";
    public static final String CONFIRMATION_EXCHANGE = "confirmation.exchange";
    public static final String CONFIRMATION_ROUTING_KEY = "confirmation.key";

    @Bean
    public Queue confirmationQueue() {
        return new Queue(CONFIRMATION_QUEUE, true);
    }

    @Bean
    public DirectExchange confirmationExchange() {
        return new DirectExchange(CONFIRMATION_EXCHANGE);
    }

    @Bean
    public Binding confirmationBinding(Queue confirmationQueue, DirectExchange confirmationExchange) {
        return BindingBuilder
                .bind(confirmationQueue)
                .to(confirmationExchange)
                .with(CONFIRMATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

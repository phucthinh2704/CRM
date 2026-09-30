package com.example.crm.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "crm.event.exchange";
    public static final String QUEUE_CUSTOMER = "rms.customer.sync.queue";
    public static final String ROUTING_KEY_CUSTOMER = "crm.customer.*";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Hỗ trợ đọc/ghi LocalDateTime
        return mapper;
    }

    @Bean
    public TopicExchange crmExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue customerQueue() {
        return QueueBuilder.durable(QUEUE_CUSTOMER).build();
    }

    @Bean
    public Binding bindingCustomer(Queue customerQueue, TopicExchange crmExchange) {
        return BindingBuilder.bind(customerQueue).to(crmExchange).with(ROUTING_KEY_CUSTOMER);
    }
}
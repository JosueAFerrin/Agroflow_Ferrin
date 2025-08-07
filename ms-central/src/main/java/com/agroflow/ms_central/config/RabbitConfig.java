package com.agroflow.ms_central.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_COSECHAS = "cosechas";
    public static final String ROUTING_KEY_NUEVA = "nueva";
    public static final String COLA_INVENTARIO = "cola-inventario";
    public static final String COLA_FACTURACION = "cola-facturacion";

    @Bean
    public DirectExchange cosechasExchange() {
        return new DirectExchange(EXCHANGE_COSECHAS);
    }

    @Bean
    public Queue colaInventario() {
        return QueueBuilder.durable(COLA_INVENTARIO).build();
    }

    @Bean
    public Queue colaFacturacion() {
        return QueueBuilder.durable(COLA_FACTURACION).build();
    }

    @Bean
    public Binding bindingInventario(Queue colaInventario, DirectExchange cosechasExchange) {
        return BindingBuilder.bind(colaInventario)
                .to(cosechasExchange)
                .with(ROUTING_KEY_NUEVA);
    }

    @Bean
    public Binding bindingFacturacion(Queue colaFacturacion, DirectExchange cosechasExchange) {
        return BindingBuilder.bind(colaFacturacion)
                .to(cosechasExchange)
                .with(ROUTING_KEY_NUEVA);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
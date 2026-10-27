package com.pedidos670.ms_catalogo.config;

import com.pedidos670.ms_catalogo.messaging.RabbitNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(
                RabbitNames.PEDIDOS_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue catalogoStockQueue() {
        return QueueBuilder
                .durable(RabbitNames.CATALOGO_STOCK_QUEUE)
                .build();
    }

    @Bean
    public Binding catalogoStockBinding(
            @Qualifier("catalogoStockQueue") Queue catalogoStockQueue,
            @Qualifier("pedidosExchange") TopicExchange pedidosExchange
    ) {
        return BindingBuilder
                .bind(catalogoStockQueue)
                .to(pedidosExchange)
                .with(RabbitNames.PEDIDO_ACEPTADO_ROUTING_KEY);
    }

    @Bean
    public DirectExchange pedidosDeadLetterExchange() {
        return new DirectExchange(
                RabbitNames.PEDIDOS_DLX,
                true,
                false
        );
    }

    @Bean
    public Queue catalogoStockDlq() {
        return QueueBuilder
                .durable(RabbitNames.CATALOGO_STOCK_DLQ)
                .build();
    }

    @Bean
    public Binding catalogoStockDlqBinding(
            @Qualifier("catalogoStockDlq") Queue catalogoStockDlq,
            @Qualifier("pedidosDeadLetterExchange")
            DirectExchange pedidosDeadLetterExchange
    ) {
        return BindingBuilder
                .bind(catalogoStockDlq)
                .to(pedidosDeadLetterExchange)
                .with(RabbitNames.CATALOGO_STOCK_DLQ_ROUTING_KEY);
    }
}
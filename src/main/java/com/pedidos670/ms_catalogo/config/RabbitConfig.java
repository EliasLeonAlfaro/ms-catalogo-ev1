package com.pedidos670.ms_catalogo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PEDIDOS =
            "pedidos.events";

    public static final String QUEUE_CATALOGO_STOCK =
            "catalogo.stock.queue";

    public static final String ROUTING_PEDIDO_ACEPTADO =
            "pedido.aceptado";


    public static final String DLX_PEDIDOS =
            "pedidos.dlx";

    public static final String DLQ_CATALOGO_STOCK =
            "catalogo.stock.dlq";

    public static final String DLQ_ROUTING_KEY =
            "catalogo.stock.dlq";


    @Bean
    public TopicExchange pedidosExchange() {

        return new TopicExchange(
                EXCHANGE_PEDIDOS,
                true,
                false
        );
    }

    @Bean
    public Queue catalogoStockQueue() {

        return QueueBuilder
                .durable(QUEUE_CATALOGO_STOCK)
                .build();
    }

    @Bean
    public Binding catalogoStockBinding(
            Queue catalogoStockQueue,
            TopicExchange pedidosExchange
    ) {

        return BindingBuilder
                .bind(catalogoStockQueue)
                .to(pedidosExchange)
                .with(ROUTING_PEDIDO_ACEPTADO);
    }


    /*
     * DEAD LETTER
     */

    @Bean
    public DirectExchange pedidosDeadLetterExchange() {

        return new DirectExchange(
                DLX_PEDIDOS,
                true,
                false
        );
    }

    @Bean
    public Queue catalogoStockDeadLetterQueue() {

        return QueueBuilder
                .durable(DLQ_CATALOGO_STOCK)
                .build();
    }

    @Bean
    public Binding catalogoStockDeadLetterBinding(
            Queue catalogoStockDeadLetterQueue,
            DirectExchange pedidosDeadLetterExchange
    ) {

        return BindingBuilder
                .bind(catalogoStockDeadLetterQueue)
                .to(pedidosDeadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }


    /*
     * Fuerza la creación de la topología
     * aunque el listener todavía esté desactivado.
     */
    @Bean
    public ApplicationRunner initializeRabbitMQ(
            ConnectionFactory connectionFactory,
            TopicExchange pedidosExchange,
            Queue catalogoStockQueue,
            Binding catalogoStockBinding,
            DirectExchange pedidosDeadLetterExchange,
            Queue catalogoStockDeadLetterQueue,
            Binding catalogoStockDeadLetterBinding
    ) {

        return args -> {

            RabbitAdmin rabbitAdmin =
                    new RabbitAdmin(connectionFactory);

            rabbitAdmin.declareExchange(
                    pedidosExchange
            );

            rabbitAdmin.declareQueue(
                    catalogoStockQueue
            );

            rabbitAdmin.declareBinding(
                    catalogoStockBinding
            );

            rabbitAdmin.declareExchange(
                    pedidosDeadLetterExchange
            );

            rabbitAdmin.declareQueue(
                    catalogoStockDeadLetterQueue
            );

            rabbitAdmin.declareBinding(
                    catalogoStockDeadLetterBinding
            );

            System.out.println(
                    "RabbitMQ ms-catalogo inicializado correctamente"
            );

            System.out.println(
                    "Queue: " + QUEUE_CATALOGO_STOCK
            );

            System.out.println(
                    "DLQ: " + DLQ_CATALOGO_STOCK
            );
        };
    }
}
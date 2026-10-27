package com.pedidos670.ms_catalogo.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos670.ms_catalogo.config.RabbitConfig;
import com.pedidos670.ms_catalogo.event.PedidoAceptadoEvent;
import com.pedidos670.ms_catalogo.Entidades.ProcessedEvent;
import com.pedidos670.ms_catalogo.Repositorio.ProcessedEventRepository;
import com.pedidos670.ms_catalogo.Repositorio.ProductoRepositorio;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final ObjectMapper objectMapper;
    private final ProductoRepositorio productoRepositorio;
    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(
            queues = RabbitNames.CATALOGO_STOCK_QUEUE,
            autoStartup = "${APP_RABBITMQ_LISTENER_ENABLED:false}"
    )
    @Transactional
    public void procesarPedidoAceptado(
            Message message,
            Channel channel
    ) throws Exception {

        long deliveryTag =
                message.getMessageProperties()
                        .getDeliveryTag();

        PedidoAceptadoEvent evento;

        try {

            evento = objectMapper.readValue(
                    message.getBody(),
                    PedidoAceptadoEvent.class
            );

        } catch (Exception ex) {

            log.error(
                    "Mensaje RabbitMQ inválido. No se puede deserializar. deliveryTag={}",
                    deliveryTag,
                    ex
            );

            rejectToDlq(
                    channel,
                    deliveryTag,
                    "JSON inválido"
            );

            return;
        }

        if (evento.getEventId() == null) {

            rejectToDlq(
                    channel,
                    deliveryTag,
                    "El evento no contiene eventId"
            );

            return;
        }

        if (!"PEDIDO_ACEPTADO".equals(
                evento.getEventType()
        )) {

            rejectToDlq(
                    channel,
                    deliveryTag,
                    "Tipo de evento no soportado: "
                            + evento.getEventType()
            );

            return;
        }

        if (
                evento.getVersion() == null
                        || evento.getVersion() != 1
        ) {

            rejectToDlq(
                    channel,
                    deliveryTag,
                    "Versión de evento no soportada: "
                            + evento.getVersion()
            );

            return;
        }

        if (
                evento.getProductoId() == null
                        || evento.getCantidad() == null
                        || evento.getCantidad() <= 0
        ) {

            rejectToDlq(
                    channel,
                    deliveryTag,
                    "Producto o cantidad inválidos"
            );

            return;
        }

        if (
                processedEventRepository.existsById(
                        evento.getEventId()
                )
        ) {

            log.warn(
                    "Evento duplicado ignorado. eventId={}",
                    evento.getEventId()
            );

            registerAckAfterCommit(
                    channel,
                    deliveryTag,
                    evento
            );

            return;
        }

        try {

            int filasActualizadas =
                    productoRepositorio.reducirStockAtomico(
                            evento.getProductoId(),
                            evento.getCantidad()
                    );

            if (filasActualizadas == 0) {

                boolean productoExiste =
                        productoRepositorio.existsById(
                                evento.getProductoId()
                        );

                if (!productoExiste) {

                    rejectToDlq(
                            channel,
                            deliveryTag,
                            "Producto no encontrado: "
                                    + evento.getProductoId()
                    );

                    return;
                }

                rejectToDlq(
                        channel,
                        deliveryTag,
                        "Stock insuficiente para producto "
                                + evento.getProductoId()
                );

                return;
            }

            ProcessedEvent processedEvent =
                    new ProcessedEvent(
                            evento.getEventId(),
                            LocalDateTime.now()
                    );

            processedEventRepository.save(
                    processedEvent
            );

            registerAckAfterCommit(
                    channel,
                    deliveryTag,
                    evento
            );

            log.info(
                    "Evento procesado correctamente. eventId={}, pedidoId={}, productoId={}, cantidad={}",
                    evento.getEventId(),
                    evento.getPedidoId(),
                    evento.getProductoId(),
                    evento.getCantidad()
            );

        } catch (Exception ex) {

            log.error(
                    "Error técnico procesando evento. Se permitirá retry. eventId={}, pedidoId={}",
                    evento.getEventId(),
                    evento.getPedidoId(),
                    ex
            );

            throw ex;
        }
    }

    private void registerAckAfterCommit(
            Channel channel,
            long deliveryTag,
            PedidoAceptadoEvent evento
    ) {

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {

                            channel.basicAck(
                                    deliveryTag,
                                    false
                            );

                            log.info(
                                    "ACK enviado. eventId={}, deliveryTag={}",
                                    evento.getEventId(),
                                    deliveryTag
                            );

                        } catch (IOException ex) {

                            log.error(
                                    "No fue posible enviar ACK. eventId={}, deliveryTag={}",
                                    evento.getEventId(),
                                    deliveryTag,
                                    ex
                            );
                        }
                    }
                }
        );
    }

    private void rejectToDlq(
            Channel channel,
            long deliveryTag,
            String reason
    ) throws IOException {

        log.warn(
                "Mensaje rechazado. Será enviado a DLQ. deliveryTag={}, reason={}",
                deliveryTag,
                reason
        );

        channel.basicNack(
                deliveryTag,
                false,
                false
        );
    }
}
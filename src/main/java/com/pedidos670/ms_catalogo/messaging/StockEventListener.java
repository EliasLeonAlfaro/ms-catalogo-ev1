package com.pedidos670.ms_catalogo.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos670.ms_catalogo.config.RabbitConfig;
import com.pedidos670.ms_catalogo.event.PedidoAceptadoEvent;
import com.pedidos670.ms_catalogo.Entidades.ProcessedEvent;
import com.pedidos670.ms_catalogo.Repositorio.ProcessedEventRepository;
import com.pedidos670.ms_catalogo.Repositorio.ProductoRepositorio;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class StockEventListener {

    private final ObjectMapper objectMapper;
    private final ProductoRepositorio productoRepositorio;
    private final ProcessedEventRepository processedEventRepository;

    public StockEventListener(
            ObjectMapper objectMapper,
            ProductoRepositorio productoRepositorio,
            ProcessedEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.productoRepositorio = productoRepositorio;
        this.processedEventRepository = processedEventRepository;
    }

    @RabbitListener(
            queues = RabbitConfig.QUEUE_CATALOGO_STOCK,
            autoStartup = "${APP_RABBITMQ_LISTENER_ENABLED:false}"
    )
    @Transactional
    public void procesarPedidoAceptado(Message message) throws Exception {

        PedidoAceptadoEvent evento =
                objectMapper.readValue(
                        message.getBody(),
                        PedidoAceptadoEvent.class
                );

        if (evento.getEventId() == null) {
            throw new IllegalArgumentException(
                    "El evento no contiene eventId"
            );
        }

        if (!"PEDIDO_ACEPTADO".equals(evento.getEventType())) {
            throw new IllegalArgumentException(
                    "Tipo de evento no soportado: "
                            + evento.getEventType()
            );
        }

        if (
                evento.getVersion() == null
                        || evento.getVersion() != 1
        ) {
            throw new IllegalArgumentException(
                    "Versión de evento no soportada: "
                            + evento.getVersion()
            );
        }

        if (
                evento.getProductoId() == null
                        || evento.getCantidad() == null
                        || evento.getCantidad() <= 0
        ) {
            throw new IllegalArgumentException(
                    "Datos de producto/cantidad inválidos"
            );
        }

        if (
                processedEventRepository.existsById(
                        evento.getEventId()
                )
        ) {
            System.out.println(
                    "Evento ya procesado. Se ignora. eventId="
                            + evento.getEventId()
            );

            return;
        }

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
                throw new IllegalStateException(
                        "Producto no encontrado: "
                                + evento.getProductoId()
                );
            }

            throw new IllegalStateException(
                    "Stock insuficiente para producto "
                            + evento.getProductoId()
                            + ", cantidad solicitada: "
                            + evento.getCantidad()
            );
        }

        ProcessedEvent processedEvent =
                new ProcessedEvent(
                        evento.getEventId(),
                        LocalDateTime.now()
                );

        processedEventRepository.save(
                processedEvent
        );

        System.out.println(
                "Evento procesado correctamente"
                        + " | eventId=" + evento.getEventId()
                        + " | pedidoId=" + evento.getPedidoId()
                        + " | productoId=" + evento.getProductoId()
                        + " | cantidad=" + evento.getCantidad()
        );
    }
}
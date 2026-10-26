package com.pedidos670.ms_catalogo.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class PedidoAceptadoEvent {

    private UUID eventId;

    private String eventType;

    private Integer version;

    private Long pedidoId;

    private Long productoId;

    private Integer cantidad;

    private LocalDateTime occurredAt;


    public PedidoAceptadoEvent() {
    }


    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }


    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }


    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(
            LocalDateTime occurredAt
    ) {
        this.occurredAt = occurredAt;
    }
}

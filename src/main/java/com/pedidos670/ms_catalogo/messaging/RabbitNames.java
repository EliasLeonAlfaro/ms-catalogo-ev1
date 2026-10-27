package com.pedidos670.ms_catalogo.messaging;

public final class RabbitNames {

    private RabbitNames() {
    }

    public static final String PEDIDOS_EXCHANGE =
            "pedidos.events";

    public static final String CATALOGO_STOCK_QUEUE =
            "catalogo.stock.queue";

    public static final String PEDIDO_ACEPTADO_ROUTING_KEY =
            "pedido.aceptado";

    public static final String PEDIDOS_DLX =
            "pedidos.dlx";

    public static final String CATALOGO_STOCK_DLQ =
            "catalogo.stock.dlq";

    public static final String CATALOGO_STOCK_DLQ_ROUTING_KEY =
            "catalogo.stock.dlq";
}
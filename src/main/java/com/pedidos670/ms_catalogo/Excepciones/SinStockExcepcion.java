package com.pedidos670.ms_catalogo.Excepciones;

public class SinStockExcepcion extends RuntimeException {
    public SinStockExcepcion(String message) {
        super(message);
    }
}

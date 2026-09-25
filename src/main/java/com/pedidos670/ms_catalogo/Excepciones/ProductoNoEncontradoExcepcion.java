package com.pedidos670.ms_catalogo.Excepciones;

// Excepcion lanzada cuando se busca o actualiza un ID que no existe
public class ProductoNoEncontradoExcepcion extends RuntimeException {
    public ProductoNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}

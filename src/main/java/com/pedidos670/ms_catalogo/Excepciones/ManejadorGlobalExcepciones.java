package com.pedidos670.ms_catalogo.Excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Intercepta las excepciones no controladas en los controladores y devuelve un JSON ordenado
@RestControllerAdvice
public class ManejadorGlobalExcepciones {
    //maneja errores de busqueda de productos
    @ExceptionHandler(ProductoNoEncontradoExcepcion.class)
    public ResponseEntity<Map<String, Object>> manejarProductoNoEncontrado(ProductoNoEncontradoExcepcion ex){
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("codigo", HttpStatus.NOT_FOUND.value());
        respuesta.put("mensaje", ex.getMessage());

        return  new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    //maneja los errores cuando se supera el stock dispo
    @ExceptionHandler(SinStockExcepcion.class)
    public ResponseEntity<Map<String, Object>> manejarSinStock(SinStockExcepcion ex){
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("codigo", HttpStatus.BAD_REQUEST.value());
        respuesta.put("mensaje", ex.getMessage());

        return  new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}

package com.pedidos670.ms_catalogo.Controlador;

import com.pedidos670.ms_catalogo.DTOs.GuardarProductoDTO;
import com.pedidos670.ms_catalogo.DTOs.RespuestaProductoDTO;
import com.pedidos670.ms_catalogo.Servicios.CatalogoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoControlador {
    private final CatalogoServicio catalogoServicio;
    //listar productos
    @GetMapping("/productos")
    public ResponseEntity<List<RespuestaProductoDTO>> listarProductos(){
        List<RespuestaProductoDTO> lista = catalogoServicio.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<RespuestaProductoDTO> buscarProductoPorId(@PathVariable Long id){
        RespuestaProductoDTO producto = catalogoServicio.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos")
    public ResponseEntity<RespuestaProductoDTO> registrarProductoPorId(@RequestBody GuardarProductoDTO dto){
        RespuestaProductoDTO creado = catalogoServicio.crearJuego(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<RespuestaProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody GuardarProductoDTO dto){
        RespuestaProductoDTO actualizado = catalogoServicio.actualizarJuego(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    @PatchMapping("/productos/{id}/stock")
    public ResponseEntity<RespuestaProductoDTO> modificarStock(@PathVariable Long id, @RequestParam Integer cantidad){
        RespuestaProductoDTO resultado = catalogoServicio.descontarStock(id, cantidad);
        return ResponseEntity.ok(resultado);
    }
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id){
        catalogoServicio.eliminarJuego(id);
        return ResponseEntity.noContent().build();
    }
}

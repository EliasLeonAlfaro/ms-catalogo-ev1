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
@RequestMapping("/api/catalogo/productos")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
})
@RequiredArgsConstructor
public class CatalogoControlador {

    private final CatalogoServicio catalogoServicio;

    // GET /api/catalogo/productos
    @GetMapping
    public ResponseEntity<List<RespuestaProductoDTO>> listarProductos(){
        List<RespuestaProductoDTO> lista = catalogoServicio.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    // GET /api/catalogo/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaProductoDTO> buscarProductoPorId(@PathVariable Long id){
        RespuestaProductoDTO producto = catalogoServicio.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    // POST /api/catalogo/productos
    @PostMapping
    public ResponseEntity<RespuestaProductoDTO> registrarProductoPorId(@RequestBody GuardarProductoDTO dto){
        RespuestaProductoDTO creado = catalogoServicio.crearJuego(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/catalogo/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody GuardarProductoDTO dto){
        RespuestaProductoDTO actualizado = catalogoServicio.actualizarJuego(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // PATCH /api/catalogo/productos/{id}/stock
    @PutMapping("/{id}/stock")
    public ResponseEntity<RespuestaProductoDTO> modificarStock(@PathVariable Long id, @RequestParam Integer cantidad){
        RespuestaProductoDTO resultado = catalogoServicio.descontarStock(id, cantidad);
        return ResponseEntity.ok(resultado);
    }

    // DELETE /api/catalogo/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id){
        catalogoServicio.eliminarJuego(id);
        return ResponseEntity.noContent().build();
    }
}
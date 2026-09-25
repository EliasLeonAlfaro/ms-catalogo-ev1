package com.pedidos670.ms_catalogo.Servicios;

import com.pedidos670.ms_catalogo.DTOs.GuardarProductoDTO;
import com.pedidos670.ms_catalogo.DTOs.RespuestaProductoDTO;
import com.pedidos670.ms_catalogo.Entidades.Producto;
import com.pedidos670.ms_catalogo.Excepciones.ProductoNoEncontradoExcepcion;
import com.pedidos670.ms_catalogo.Excepciones.SinStockExcepcion;
import com.pedidos670.ms_catalogo.Repositorio.ProductoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //genera de forma automatica el cosntructor con las dependencias
public class CatalogoServicio {
    private final ProductoRepositorio productoRepositorio;
    //metodo privado para mapear entidad
    private RespuestaProductoDTO convertirDto(Producto producto){
        return RespuestaProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .formato(producto.getFormato())
                .build();
    }

    //obtener lista de productos
    public List<RespuestaProductoDTO> obtenerTodos(){
        List<Producto> listaProductos = productoRepositorio.findAll();
        List<RespuestaProductoDTO> resultado = new ArrayList<>();

        for (Producto p : listaProductos){
            resultado.add(convertirDto(p));
        }
        return resultado;
    }
    //obtener un videojuego especifico mediante su ID
    public RespuestaProductoDTO obtenerPorId(Long id){
        Producto juego = productoRepositorio.findById(id).orElseThrow(()-> new ProductoNoEncontradoExcepcion("El juego no fue encontrado"));
                return convertirDto(juego);
    }

    //guardar un nuevo juego en BD
    public RespuestaProductoDTO crearJuego(GuardarProductoDTO dto){
        Producto nuevoJuego = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .formato(dto.getFormato())
                .build();
        Producto juegoGuardado = productoRepositorio.save(nuevoJuego);
        return convertirDto(juegoGuardado);
    }
    //moficiar producto existente
    public RespuestaProductoDTO actualizarJuego(Long id, GuardarProductoDTO dto){
        Producto juegoExistente = productoRepositorio.findById(id).orElseThrow(()-> new ProductoNoEncontradoExcepcion("No se puede un producto inexistente"));
        juegoExistente.setNombre(dto.getNombre());
        juegoExistente.setDescripcion(dto.getDescripcion());
        juegoExistente.setPrecio(dto.getPrecio());
        juegoExistente.setStock(dto.getStock());
        juegoExistente.setFormato(dto.getFormato());

        return convertirDto(productoRepositorio.save(juegoExistente));
    }

    //descuento de inv por compra realizada
    public RespuestaProductoDTO descontarStock(Long id, Integer cantidadAComprar){
        Producto juego = productoRepositorio.findById(id).orElseThrow(()-> new ProductoNoEncontradoExcepcion("Juego no encontrado"));
        // validar si hay inventario dispo
        if (juego.getStock() < cantidadAComprar){
            throw new SinStockExcepcion("Stock insuficiente. A comprar:" + cantidadAComprar +", Disponible:" + juego.getStock());
        }

        //restar de inventario y guardar
        juego.setStock(juego.getStock() - cantidadAComprar);
        return convertirDto(productoRepositorio.save(juego));
    }

    //eliminar producto
    public void eliminarJuego(Long id){
        if(!productoRepositorio.existsById(id)){
            throw new ProductoNoEncontradoExcepcion("No se puede eliminar un producto inexistente");

        }
        productoRepositorio.deleteById(id);

    }
}

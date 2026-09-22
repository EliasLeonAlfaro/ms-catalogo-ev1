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

}

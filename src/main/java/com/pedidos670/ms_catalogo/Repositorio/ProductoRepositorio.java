package com.pedidos670.ms_catalogo.Repositorio;
import com.pedidos670.ms_catalogo.Entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface ProductoRepositorio  extends JpaRepository<Producto, Long>{
}

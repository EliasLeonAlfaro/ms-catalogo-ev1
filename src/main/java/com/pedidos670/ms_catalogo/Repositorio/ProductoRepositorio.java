package com.pedidos670.ms_catalogo.Repositorio;



import com.pedidos670.ms_catalogo.Entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    @Modifying
    @Query("""
            UPDATE Producto p
            SET p.stock = p.stock - :cantidad
            WHERE p.id = :productoId
              AND p.stock >= :cantidad
            """)
    int reducirStockAtomico(
            @Param("productoId") Long productoId,
            @Param("cantidad") Integer cantidad
    );
}


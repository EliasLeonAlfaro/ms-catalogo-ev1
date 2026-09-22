package com.pedidos670.ms_catalogo.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
// hace refencia a la tabla creada en la base de datos
@Table(name = "PRODUCTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna para el nombre del videojuego
    @Column(name = "NAME", nullable = false, length = 150)
    private String nombre;

    // Breve descripcion del juego
    @Column(name = "DESCRIPTION", length = 500)
    private String descripcion;

    // Precio con decimales
    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Unidades disponibles en inventario
    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    // Tipo de producto: 'FISICO' o 'DIGITAL'[cite: 1]
    @Column(name = "FORMAT", nullable = false, length = 20)
    private String formato;
}

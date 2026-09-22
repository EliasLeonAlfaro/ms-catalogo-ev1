package com.pedidos670.ms_catalogo.DTOs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardarProductoDTO {
    //Datos que se deben recibir desde el front o BFF para crear un juego o editarlo
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String format;
}

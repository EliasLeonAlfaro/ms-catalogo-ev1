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
public class RespuestaProductoDTO {
    //objeto expuesto para no devolevr la entidad JPA
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String format;
}

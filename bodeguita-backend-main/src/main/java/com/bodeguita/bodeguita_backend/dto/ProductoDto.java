package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record ProductoDto(
        Long id,
        Long idCategoria,
        String nombreCategoria,
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precioVenta,
        Integer stock,
        Integer stockMinimo,
        String unidadMedida
) {
}

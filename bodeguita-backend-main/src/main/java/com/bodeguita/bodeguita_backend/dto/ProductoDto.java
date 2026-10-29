package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record ProductoDto(
        Long id,
        Long idCategoria,
        String nombreCategoria,
        String codigoBarras,
        String nombre,
        String descripcion,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal precioMayoreo,
        Integer stock,
        Integer stockMinimo,
        Integer stockMaximo,
        String afectoIgv,
        String esPerecible,
        String imagen
) {
}

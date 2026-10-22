package com.bodeguita.bodeguita_backend.dto;

public record ProductoStockBajoDto(
        Long idProducto,
        String codigo,
        String nombre,
        Integer stock,
        Integer stockMinimo
) {
}

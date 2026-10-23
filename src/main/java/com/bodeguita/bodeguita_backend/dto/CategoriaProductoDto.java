package com.bodeguita.bodeguita_backend.dto;

public record CategoriaProductoDto(
        Long idCategoriaProducto,
        String nCategoriaProducto,
        String descripcion,
        Boolean estado) {
}

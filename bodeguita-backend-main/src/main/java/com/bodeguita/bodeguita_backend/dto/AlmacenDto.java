package com.bodeguita.bodeguita_backend.dto;

public record AlmacenDto(
        Long id,
        String nombre,
        String descripcion,
        String ubicacion,
        String esPrincipal,
        String estado
) {
}

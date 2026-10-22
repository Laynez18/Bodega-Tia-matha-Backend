package com.bodeguita.bodeguita_backend.dto;

public record TipoMovimientoCajaDto(
        Long id,
        String nombre,
        String abreviatura,
        String signo
) {
}

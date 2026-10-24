package com.bodeguita.bodeguita_backend.dto;

public record TipoMovimientoInvDto(
        Long id,
        String nombre,
        String abreviatura,
        String signo,
        String estado
) {
}

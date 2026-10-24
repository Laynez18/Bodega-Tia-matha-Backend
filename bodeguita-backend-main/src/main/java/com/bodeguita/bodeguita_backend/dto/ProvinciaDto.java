package com.bodeguita.bodeguita_backend.dto;

public record ProvinciaDto(
        Long id,
        Long idDepartamento,
        String nombre,
        String estado
) {
}

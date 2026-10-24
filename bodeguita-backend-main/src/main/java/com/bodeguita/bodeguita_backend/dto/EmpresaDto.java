package com.bodeguita.bodeguita_backend.dto;

public record EmpresaDto(
        Long id,
        String ruc,
        String razonSocial,
        String direccion,
        String telefono,
        String estado
) {
}

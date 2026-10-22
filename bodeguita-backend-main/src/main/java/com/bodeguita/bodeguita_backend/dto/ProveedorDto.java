package com.bodeguita.bodeguita_backend.dto;

public record ProveedorDto(
        Long id,
        Long idPersona,
        String nombrePersona,
        String nDocumento,
        String razonSocial,
        String telefono,
        String email,
        String direccion
) {
}

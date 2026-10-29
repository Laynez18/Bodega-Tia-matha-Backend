package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record ProveedorDto(
        Long id,
        Long idPersona,
        String nombrePersona,
        String nDocumento,
        Long idEmpresa,
        String codigoProveedor,
        String contacto,
        String telefonoContacto,
        String emailContacto,
        Integer diasCredito
) {
}

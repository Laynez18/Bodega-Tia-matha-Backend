package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record ProveedorDto(
        Long idProveedor,
        PersonaDto persona,
        Long idEmpresa,
        String codigoProveedor,
        String contacto,
        String telefonoContacto,
        String emailContacto,
        Integer diasCredito,
        LocalDateTime fRegistro,
        Boolean estado) {
}

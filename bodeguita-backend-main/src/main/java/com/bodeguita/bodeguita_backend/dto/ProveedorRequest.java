package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ProveedorRequest(
        @NotNull Long idPersona,
        Long idEmpresa,
        @Size(max = 20) String codigoProveedor,
        @Size(max = 100) String contacto,
        @Size(max = 15) String telefonoContacto,
        @Size(max = 100) String emailContacto,
        Integer diasCredito
) {
}

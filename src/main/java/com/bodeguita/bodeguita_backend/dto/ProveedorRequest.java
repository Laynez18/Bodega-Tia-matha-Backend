package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(
        @NotNull @Valid PersonaRequest persona,
        Long idEmpresa,
        @Size(max = 20) String codigoProveedor,
        @Size(max = 80) String contacto,
        @Size(max = 15) String telefonoContacto,
        @Email @Size(max = 50) String emailContacto,
        @PositiveOrZero Integer diasCredito) {
}

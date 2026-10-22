package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(
        @NotNull Long idPersona,
        @Size(max = 120) String razonSocial,
        @Size(max = 15) String telefono,
        @Size(max = 100) String email,
        @Size(max = 200) String direccion
) {
}

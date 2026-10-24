package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank @Size(max = 20) String ruc,
        @NotBlank @Size(max = 200) String razonSocial,
        @Size(max = 200) String direccion,
        @Size(max = 20) String telefono
) {
}

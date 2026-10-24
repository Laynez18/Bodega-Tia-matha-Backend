package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoMovimientoInvRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 10) String abreviatura,
        @Size(max = 1) String signo
) {
}

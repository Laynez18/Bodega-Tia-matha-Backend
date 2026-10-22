package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoMovimientoCajaRequest(
    @NotBlank @Size(max = 30) String nombre,
    @Size(max = 10) String abreviatura,
    String signo
) {
}

package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MetodoPagoRequest(
    @NotBlank @Size(max = 30) String nombre,
    Boolean estado
) {
}

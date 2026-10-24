package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadMedidaRequest(
        @NotBlank @Size(max = 50) String nombre,
        @Size(max = 10) String abreviatura
) {
}

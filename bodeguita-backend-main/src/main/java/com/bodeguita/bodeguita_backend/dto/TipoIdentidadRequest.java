package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TipoIdentidadRequest(
        @NotBlank @Size(max = 20) String nTipoIdentidad,
        @Size(max = 10) String abreviatura,
        @Positive Integer longitud) {
}

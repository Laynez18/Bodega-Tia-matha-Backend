package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarcaRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 200) String descripcion
) {
}

package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProvinciaRequest(
        @NotNull Long idDepartamento,
        @NotBlank @Size(max = 100) String nombre
) {
}


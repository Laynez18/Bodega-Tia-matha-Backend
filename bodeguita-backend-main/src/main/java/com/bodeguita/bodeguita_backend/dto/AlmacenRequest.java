package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlmacenRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 200) String descripcion,
        @Size(max = 200) String ubicacion,
        @Size(max = 1) String esPrincipal
) {
}

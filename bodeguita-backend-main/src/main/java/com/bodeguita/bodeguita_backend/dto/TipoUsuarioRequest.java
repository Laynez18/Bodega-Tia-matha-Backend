package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoUsuarioRequest(
        @NotBlank @Size(max = 50) String nTipoUsuario
) {
}

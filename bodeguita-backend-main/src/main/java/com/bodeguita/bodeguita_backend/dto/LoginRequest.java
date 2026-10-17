package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 30, message = "El usuario no puede pasar de 30 caracteres")
        String logeo,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(max = 200, message = "La contrasena no puede pasar de 200 caracteres")
        String clave
) {
}

package com.bodeguita.bodeguita_backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotNull Long idTipoUsuario,
        Long idEmpleado,
        @NotBlank @Size(max = 30) String logeo,
        @Size(min = 8, max = 100) String clave,
        List<@NotNull Long> idsRol) {
}

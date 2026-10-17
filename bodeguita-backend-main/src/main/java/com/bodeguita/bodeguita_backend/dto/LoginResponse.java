package com.bodeguita.bodeguita_backend.dto;

import java.util.List;

public record LoginResponse(
        Long idUsuario,
        String logeo,
        String nombreCompleto,
        Long idTipoUsuario,
        List<ModuloDto> modulos,
        List<String> permisos,
        String csrfToken,
        String csrfHeader
) {
}

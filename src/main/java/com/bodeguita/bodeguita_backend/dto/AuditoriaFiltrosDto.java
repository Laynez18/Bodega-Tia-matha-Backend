package com.bodeguita.bodeguita_backend.dto;

import java.util.List;

public record AuditoriaFiltrosDto(
        List<UsuarioFiltroDto> usuarios,
        List<String> tablas,
        List<String> acciones) {
}

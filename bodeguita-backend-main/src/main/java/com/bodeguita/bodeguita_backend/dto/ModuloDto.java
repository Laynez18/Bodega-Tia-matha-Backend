package com.bodeguita.bodeguita_backend.dto;

import java.util.List;

public record ModuloDto(
        String nombre,
        String icono,
        Integer orden,
        List<String> permisos
) {
}

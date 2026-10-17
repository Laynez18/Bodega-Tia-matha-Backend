package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioDto(
        Long idUsuario,
        Long idTipoUsuario,
        String nTipoUsuario,
        Long idEmpleado,
        String nombreEmpleado,
        String logeo,
        List<RolResumenDto> roles,
        LocalDateTime fecCre) {
}

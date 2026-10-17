package com.bodeguita.bodeguita_backend.dto;
import java.util.List;
public record ModuloPermisosDto(Long idModulo, String nModulo, List<PermisoFilaDto> permisos) {}

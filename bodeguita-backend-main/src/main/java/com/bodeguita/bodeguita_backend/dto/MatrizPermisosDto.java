package com.bodeguita.bodeguita_backend.dto;
import java.util.List;
public record MatrizPermisosDto(List<RolResumenDto> roles, List<ModuloPermisosDto> modulos) {}

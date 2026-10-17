package com.bodeguita.bodeguita_backend.dto;
import java.util.Map;
public record PermisoFilaDto(Long idPermiso, String clave, String nPermiso, Map<String, Boolean> concedidoPorRol) {}

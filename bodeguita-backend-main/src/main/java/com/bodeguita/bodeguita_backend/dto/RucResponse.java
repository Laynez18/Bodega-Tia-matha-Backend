package com.bodeguita.bodeguita_backend.dto;

public record RucResponse(
        boolean success,
        String ruc,
        String razonSocial,
        String nombreComercial,
        String direccion,
        String distrito,
        String provincia,
        String departamento,
        String estadoContribuyente,
        String condicionContribuyente
) {
}

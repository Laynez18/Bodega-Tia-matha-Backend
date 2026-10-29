package com.bodeguita.bodeguita_backend.dto;

public record DniResponse(
        boolean success,
        String dni,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno
) {
}

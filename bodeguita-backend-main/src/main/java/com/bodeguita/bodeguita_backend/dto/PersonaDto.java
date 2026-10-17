package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDate;

public record PersonaDto(
        Long idPersona,
        Long idDistrito,
        Long idTipoIdentidad,
        String nDocumento,
        String nombre,
        String apPaterno,
        String apMaterno,
        LocalDate fNacimiento,
        String email,
        String celular,
        String genero,
        String direccion) {
}

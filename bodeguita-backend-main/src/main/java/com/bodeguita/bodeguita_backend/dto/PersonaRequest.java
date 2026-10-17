package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PersonaRequest(
        Long idDistrito,
        @NotNull Long idTipoIdentidad,
        @NotBlank @Size(max = 15) String nDocumento,
        @NotBlank @Size(max = 80) String nombre,
        @Size(max = 80) String apPaterno,
        @Size(max = 80) String apMaterno,
        LocalDate fNacimiento,
        @Email @Size(max = 50) String email,
        @Size(max = 9) String celular,
        @Pattern(regexp = "[MFO]") String genero,
        @Size(max = 100) String direccion) {
}

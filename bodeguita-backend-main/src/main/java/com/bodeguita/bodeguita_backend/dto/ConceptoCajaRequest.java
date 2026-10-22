package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConceptoCajaRequest(
        @NotNull Long idTipoMovimiento,
        @NotBlank @Size(max = 60) String nombre,
        @Size(max = 150) String descripcion,
        Boolean afectaEfectivo
) {
}

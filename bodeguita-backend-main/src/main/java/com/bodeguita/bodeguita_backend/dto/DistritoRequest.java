package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DistritoRequest(
        @NotNull Long idProvincia,
        @NotBlank @Size(max = 40) String dDistrito
) {
}


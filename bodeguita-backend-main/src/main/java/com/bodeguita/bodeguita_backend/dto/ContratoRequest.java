package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContratoRequest(@NotBlank @Size(max = 40) String nContrato) {
}

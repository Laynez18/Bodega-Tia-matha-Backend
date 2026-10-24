package com.bodeguita.bodeguita_backend.dto;

public record DistritoDto(
        Long idDistrito,
        Long idProvincia,
        String dDistrito,
        String estado
) {
}

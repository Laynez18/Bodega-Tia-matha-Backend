package com.bodeguita.bodeguita_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DniResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("datos") DatosDni datos
) {
    public record DatosDni(
            @JsonProperty("dni") String dni,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("apellido_paterno") String apellidoPaterno,
            @JsonProperty("apellido_materno") String apellidoMaterno
    ) {
    }
}

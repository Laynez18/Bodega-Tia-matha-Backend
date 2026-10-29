package com.bodeguita.bodeguita_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RucResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("datos") DatosRuc datos
) {
    public record DatosRuc(
            @JsonProperty("ruc") String ruc,
            @JsonProperty("razon_social") String razonSocial,
            @JsonProperty("estado") String estado,
            @JsonProperty("condicion") String condicion,
            @JsonProperty("domiciliado") Domiciliado domiciliado
    ) {
        public record Domiciliado(
                @JsonProperty("direccion") String direccion,
                @JsonProperty("distrito") String distrito,
                @JsonProperty("provincia") String provincia,
                @JsonProperty("departamento") String departamento,
                @JsonProperty("ubigeo") String ubigeo
        ) {
        }
    }
}

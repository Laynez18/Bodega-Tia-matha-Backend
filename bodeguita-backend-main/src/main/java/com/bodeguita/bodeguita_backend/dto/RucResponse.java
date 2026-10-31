package com.bodeguita.bodeguita_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RucResponse(
        @JsonProperty("status") Integer status,
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("data") DatosRuc datos
) {
    public record DatosRuc(
            @JsonProperty("numero") String numero,
            @JsonProperty("nombre_o_razon_social") String razonSocial,
            @JsonProperty("tipo_contribuyente") String tipoContribuyente,
            @JsonProperty("estado") String estado,
            @JsonProperty("condicion") String condicion,
            @JsonProperty("departamento") String departamento,
            @JsonProperty("provincia") String provincia,
            @JsonProperty("distrito") String distrito,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("direccion_completa") String direccionCompleta,
            @JsonProperty("ubigeo_sunat") String ubigeoSunat,
            @JsonProperty("ubigeo") List<String> ubigeo
    ) {
    }
}

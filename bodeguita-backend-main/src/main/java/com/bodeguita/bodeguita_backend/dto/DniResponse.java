package com.bodeguita.bodeguita_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DniResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("datos") DatosDni datos
) {
    public record DatosDni(
            @JsonProperty("dni") String dni,
            @JsonProperty("codigo_verificacion") String codigoVerificacion,
            @JsonProperty("nombres") String nombres,
            @JsonProperty("ape_paterno") String apellidoPaterno,
            @JsonProperty("ape_materno") String apellidoMaterno,
            @JsonProperty("genero") String genero,
            @JsonProperty("fecha_nacimiento") String fechaNacimiento,
            @JsonProperty("edad") Integer edad,
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

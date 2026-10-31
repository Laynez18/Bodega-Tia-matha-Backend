package com.bodeguita.bodeguita_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DniResponse(
        @JsonProperty("status") Integer status,
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("data") DatosDni datos
) {
    public record DatosDni(
            @JsonProperty("numero") String numero,
            @JsonProperty("nombres") String nombres,
            @JsonProperty("apellido_paterno") String apellidoPaterno,
            @JsonProperty("apellido_materno") String apellidoMaterno,
            @JsonProperty("nombre_completo") String nombreCompleto,
            @JsonProperty("departamento") String departamento,
            @JsonProperty("provincia") String provincia,
            @JsonProperty("distrito") String distrito,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("direccion_completa") String direccionCompleta,
            @JsonProperty("ubigeo_reniec") String ubigeoReniec,
            @JsonProperty("ubigeo_sunat") String ubigeoSunat,
            @JsonProperty("ubigeo") List<String> ubigeo,
            @JsonProperty("fecha_nacimiento") String fechaNacimiento,
            @JsonProperty("sexo") String sexo
    ) {
    }
}

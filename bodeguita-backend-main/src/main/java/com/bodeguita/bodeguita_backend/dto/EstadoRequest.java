package com.bodeguita.bodeguita_backend.dto;
import jakarta.validation.constraints.Pattern;
public record EstadoRequest(@Pattern(regexp="[01]") String estado) {}

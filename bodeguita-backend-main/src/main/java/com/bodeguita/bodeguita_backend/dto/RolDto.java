package com.bodeguita.bodeguita_backend.dto;
import java.time.LocalDateTime;
public record RolDto(Long idRol, String nRol, String descripcion, Integer nivel, LocalDateTime fCreacion, Boolean estado) {}

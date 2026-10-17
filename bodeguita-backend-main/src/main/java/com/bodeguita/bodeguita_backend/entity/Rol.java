package com.bodeguita.bodeguita_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "rol",
        uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "uq_rol_nombre", columnNames = "n_rol"))
public class Rol extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "n_rol", length = 50, nullable = false)
    private String nRol;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "nivel")
    private Integer nivel;

    @Column(name = "f_creacion")
    private LocalDateTime fCreacion;
}

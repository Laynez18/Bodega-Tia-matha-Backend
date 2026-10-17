package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "modulo")
public class Modulo extends BaseAuditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo") private Long idModulo;
    @Column(name = "n_modulo", length = 50, nullable = false) private String nModulo;
    @Column(name = "descripcion", length = 100) private String descripcion;
    @Column(name = "icono", length = 50) private String icono;
    @Column(name = "orden") private Integer orden;
}

package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipo_movimiento_caja")
public class TipoMovimientoCaja extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimiento")
    private Long id;

    @Column(name = "n_tipo_movimiento", length = 30, nullable = false)
    private String nombre;

    @Column(name = "abreviatura", length = 10)
    private String abreviatura;

    @Column(name = "signo", nullable = false)
    private String signo;

    @Column(name = "f_creacion")
    private LocalDateTime fCreacion;

    @Builder.Default
    @OneToMany(mappedBy = "tipoMovimiento")
    private List<ConceptoCaja> conceptos = new ArrayList<>();
}
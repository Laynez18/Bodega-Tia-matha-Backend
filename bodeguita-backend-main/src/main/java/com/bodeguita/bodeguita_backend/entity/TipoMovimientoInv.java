package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipo_movimiento_inv")
public class TipoMovimientoInv extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimiento_inv")
    private Long id;

    @Column(name = "n_tipo_movimiento", length = 100, nullable = false)
    private String nombre;

    @Column(name = "abreviatura", length = 10)
    private String abreviatura;

    @Column(name = "signo", length = 1, nullable = false)
    private String signo;
}

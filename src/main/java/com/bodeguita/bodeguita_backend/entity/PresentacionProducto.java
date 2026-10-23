package com.bodeguita.bodeguita_backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "presentacion_producto",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_presentacion_producto",
                columnNames = { "id_producto", "id_unidad_medida" }))
public class PresentacionProducto extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presentacion_producto")
    private Long idPresentacionProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(name = "factor_conversion", precision = 12, scale = 3, nullable = false)
    private BigDecimal factorConversion;

    @Column(name = "es_unidad_base", length = 1, nullable = false)
    private Boolean esUnidadBase;
}

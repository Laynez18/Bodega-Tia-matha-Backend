package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_inventario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_movimiento_inv", nullable = false)
    private TipoMovimientoInv tipoMovimientoInv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra compra;

    @Column(name = "cantidad", precision = 12, scale = 3, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "stock_anterior", precision = 12, scale = 3, nullable = false)
    private BigDecimal stockAnterior;

    @Column(name = "stock_nuevo", precision = 12, scale = 3, nullable = false)
    private BigDecimal stockNuevo;

    @Column(name = "f_movimiento", nullable = false)
    private LocalDateTime fMovimiento;

    @Column(name = "documento", length = 30)
    private String documento;

    @Column(name = "observacion", length = 200)
    private String observacion;
}

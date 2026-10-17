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
@Table(name = "movimiento_caja")
public class MovimientoCaja extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_caja")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apertura_caja", nullable = false)
    private AperturaCaja aperturaCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_movimiento", nullable = false)
    private TipoMovimientoCaja tipoMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto", nullable = false)
    private ConceptoCaja concepto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "id_compra")
    private Long idCompra;

    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "numero_operacion", length = 30)
    private String numeroOperacion;

    @Column(name = "documento", length = 50)
    private String documento;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Builder.Default
    @Column(name = "afecta_efectivo", nullable = false)
    private Boolean afectaEfectivo = Boolean.TRUE;

    @Column(name = "f_movimiento", nullable = false)
    private LocalDateTime fMovimiento;

    @Column(name = "ip", length = 20)
    private String ip;

    @Column(name = "terminal", length = 30)
    private String terminal;
}
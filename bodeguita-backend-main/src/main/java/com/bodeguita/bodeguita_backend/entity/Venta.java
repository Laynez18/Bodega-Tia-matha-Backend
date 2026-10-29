package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "venta")
public class Venta extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(name = "f_venta", nullable = false)
    private LocalDateTime fVenta;

    @Column(name = "sub_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "igv", precision = 12, scale = 2, nullable = false)
    private BigDecimal igv;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apertura_caja")
    private AperturaCaja aperturaCaja;

    @Column(name = "t_pagado", precision = 12, scale = 2, nullable = false)
    private BigDecimal tPagado;

    @Column(name = "vuelto", precision = 12, scale = 2, nullable = false)
    private BigDecimal vuelto;

    @Column(name = "es_credito", length = 1, nullable = false)
    private String esCredito;

    @Column(name = "saldo", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldo;

    @Column(name = "tipo_documento", length = 20, nullable = false)
    private String tipoDocumento;

    @Column(name = "situacion", length = 1, nullable = false)
    private String situacion;

    @Column(name = "observacion", length = 200)
    private String observacion;

    @Builder.Default
    @OneToMany(mappedBy = "venta")
    private List<DetalleVenta> detalles = new ArrayList<>();
}

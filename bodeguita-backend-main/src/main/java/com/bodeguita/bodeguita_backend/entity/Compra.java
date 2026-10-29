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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "compra")
public class Compra extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @Column(name = "documento", length = 50)
    private String documento;

    @Column(name = "f_compra", nullable = false)
    private LocalDateTime fCompra;

    @Column(name = "sub_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "igv", precision = 12, scale = 2, nullable = false)
    private BigDecimal igv;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "t_pagado", precision = 12, scale = 2, nullable = false)
    private BigDecimal tPagado;

    @Column(name = "saldo", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldo;

    @Column(name = "es_credito", length = 1, nullable = false)
    private String esCredito;

    @Column(name = "f_vencimiento")
    private LocalDate fVencimiento;

    @Column(name = "situacion", length = 1, nullable = false)
    private String situacion;

    @Builder.Default
    @OneToMany(mappedBy = "compra")
    private List<DetalleCompra> detalles = new ArrayList<>();
}

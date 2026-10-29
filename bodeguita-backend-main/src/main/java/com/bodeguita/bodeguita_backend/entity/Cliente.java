package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "cliente")
public class Cliente extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Column(name = "codigo_cliente", length = 20)
    private String codigoCliente;

    @Column(name = "tipo_cliente", length = 20)
    private String tipoCliente;

    @Column(name = "limite_credito", precision = 12, scale = 2)
    private BigDecimal limiteCredito;

    @Column(name = "saldo_deuda", precision = 12, scale = 2)
    private BigDecimal saldoDeuda;

    @Column(name = "puntos")
    private Integer puntos;

    @Column(name = "f_registro")
    private LocalDateTime fRegistro;

    @Column(name = "observacion", length = 200)
    private String observacion;
}

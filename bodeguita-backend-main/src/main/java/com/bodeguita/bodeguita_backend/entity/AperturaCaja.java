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
@Table(name = "apertura_caja")
public class AperturaCaja extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apertura_caja")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cierre")
    private Usuario usuarioCierre;

    @Column(name = "numero_turno", length = 20)
    private String numeroTurno;

    @Column(name = "f_apertura", nullable = false)
    private LocalDateTime fApertura;

    @Column(name = "f_cierre")
    private LocalDateTime fCierre;

    @Column(name = "monto_inicial", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoInicial;

    @Column(name = "total_ingresos", precision = 12, scale = 2)
    private BigDecimal totalIngresos;

    @Column(name = "total_egresos", precision = 12, scale = 2)
    private BigDecimal totalEgresos;

    @Column(name = "monto_sistema", precision = 12, scale = 2)
    private BigDecimal montoSistema;

    @Column(name = "monto_declarado", precision = 12, scale = 2)
    private BigDecimal montoDeclarado;

    @Column(name = "diferencia", precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Builder.Default
    @Column(name = "situacion", nullable = false)
    private String situacion = "A";

    @Column(name = "observacion", length = 200)
    private String observacion;

    @Builder.Default
    @OneToMany(mappedBy = "aperturaCaja")
    private List<MovimientoCaja> movimientos = new ArrayList<>();
}
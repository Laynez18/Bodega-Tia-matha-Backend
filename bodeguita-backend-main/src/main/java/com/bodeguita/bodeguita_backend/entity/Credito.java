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
@Table(name = "credito")
public class Credito extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credito")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "saldo", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldo;

    @Column(name = "f_emision", nullable = false)
    private LocalDateTime fEmision;

    @Column(name = "f_vencimiento")
    private LocalDateTime fVencimiento;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "observacion", length = 200)
    private String observacion;

    @Builder.Default
    @OneToMany(mappedBy = "credito")
    private List<PagoCredito> pagos = new ArrayList<>();
}

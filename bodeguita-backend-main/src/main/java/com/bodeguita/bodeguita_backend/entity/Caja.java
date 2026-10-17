package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;import lombok.Builder;
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
@Table(name = "caja")
public class Caja extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Long id;

    @Column(name = "n_caja", length = 50, nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "serie_terminal", length = 30)
    private String serieTerminal;

    @Builder.Default
    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda = "PEN";

    @Column(name = "monto_base", precision = 12, scale = 2)
    private BigDecimal montoBase;

    @Builder.Default
    @Column(name = "aperturada", nullable = false)
    private Boolean aperturada = Boolean.FALSE;

    @Column(name = "f_creacion")
    private LocalDateTime fCreacion;

    @Builder.Default
    @OneToMany(mappedBy = "caja")
    private List<AperturaCaja> aperturas = new ArrayList<>();
}
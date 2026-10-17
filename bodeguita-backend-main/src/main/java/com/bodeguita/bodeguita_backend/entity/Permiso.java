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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "permiso")
public class Permiso extends BaseAuditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso") private Long idPermiso;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_modulo", nullable = false) private Modulo modulo;
    @Column(name = "n_permiso", length = 50, nullable = false) private String nPermiso;
    @Column(name = "clave", length = 50, nullable = false) private String clave;
    @Column(name = "descripcion", length = 100) private String descripcion;
}

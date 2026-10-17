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
@Table(name = "rol_permiso")
public class RolPermiso extends BaseAuditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_permiso") private Long idRolPermiso;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_rol", nullable = false) private Rol rol;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_permiso", nullable = false) private Permiso permiso;
    @Column(name = "concedido", length = 1, nullable = false) private String concedido = "1";
}

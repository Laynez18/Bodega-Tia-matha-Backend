package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.bodeguita.bodeguita_backend.security.ContextoUsuario;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseAuditoria {

    private static final ZoneId ZONA_HORARIA_LIMA = ZoneId.of("America/Lima");

    @Column(name = "usu_cre", length = 30)
    private String usuarioCreador;

    @Column(name = "pc_cre", length = 30)
    private String pcCreador;

    @Column(name = "fec_cre")
    private LocalDateTime fecCreador;

    @Column(name = "usu_mod", length = 30)
    private String usuarioModificador;

    @Column(name = "pc_mod", length = 30)
    private String pcModificador;

    @Column(name = "fec_mod")
    private LocalDateTime fecModificador;

    @Column(name = "estado", length = 1, nullable = false)
    private Boolean estado = Boolean.TRUE;

    @PrePersist
    protected void antesDeCrear() {
        if (fecCreador == null) {
            fecCreador = LocalDateTime.now(ZONA_HORARIA_LIMA);
        }
        if (estado == null) {
            estado = Boolean.TRUE;
        }
        if (usuarioCreador == null) {
            usuarioCreador = ContextoUsuario.logeo();
        }
    }

    @PreUpdate
    protected void antesDeActualizar() {
        fecModificador = LocalDateTime.now(ZONA_HORARIA_LIMA);
        usuarioModificador = ContextoUsuario.logeo();
    }
}
package com.bodeguita.bodeguita_backend.entity;

import java.time.LocalDateTime;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auditoria")
public class Auditoria extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "n_tabla", length = 50, nullable = false)
    private String nTabla;

    @Column(name = "accion", length = 20, nullable = false)
    private String accion;

    @Column(name = "id_registro")
    private Long idRegistro;

    @Column(name = "valor_anterior", length = 500)
    private String valorAnterior;

    @Column(name = "valor_nuevo", length = 500)
    private String valorNuevo;

    @Column(name = "f_evento")
    private LocalDateTime fEvento;

    @Column(name = "ip", length = 20)
    private String ip;

    @Column(name = "terminal", length = 30)
    private String terminal;
}

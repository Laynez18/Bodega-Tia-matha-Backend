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
@Table(name = "proveedor")
public class Proveedor extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "codigo_proveedor", length = 20)
    private String codigoProveedor;

    @Column(name = "contacto", length = 80)
    private String contacto;

    @Column(name = "telefono_contacto", length = 15)
    private String telefonoContacto;

    @Column(name = "email_contacto", length = 50)
    private String emailContacto;

    @Column(name = "dias_credito")
    private Integer diasCredito;

    @Column(name = "f_registro")
    private LocalDateTime fRegistro;
}

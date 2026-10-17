package com.bodeguita.bodeguita_backend.entity;

import java.time.LocalDate;

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
@Table(name = "persona")
public class Persona extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_distrito")
    private Distrito distrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_identidad", nullable = false)
    private TipoIdentidad tipoIdentidad;

    @Column(name = "n_documento", length = 15, nullable = false)
    private String nDocumento;

    @Column(name = "nombre", length = 80, nullable = false)
    private String nombre;

    @Column(name = "ap_paterno", length = 80)
    private String apPaterno;

    @Column(name = "ap_materno", length = 80)
    private String apMaterno;

    @Column(name = "f_nacimiento")
    private LocalDate fNacimiento;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "celular", length = 9)
    private String celular;

    @Column(name = "genero", length = 1)
    private String genero;

    @Column(name = "direccion", length = 100)
    private String direccion;
}

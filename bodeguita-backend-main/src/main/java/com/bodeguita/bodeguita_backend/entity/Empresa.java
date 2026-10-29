package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "empresa")
public class Empresa extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "ruc", length = 11, nullable = false)
    private String ruc;

    @Column(name = "razon_social", length = 140, nullable = false)
    private String razonSocial;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "telefono", length = 8)
    private String telefono;
}

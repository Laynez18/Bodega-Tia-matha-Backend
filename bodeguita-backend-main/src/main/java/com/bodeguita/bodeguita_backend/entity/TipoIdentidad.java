package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tipo_identidad")
public class TipoIdentidad extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_identidad")
    private Long idTipoIdentidad;

    @Column(name = "n_tipo_identidad", length = 20, nullable = false)
    private String nTipoIdentidad;

    @Column(name = "abreviatura", length = 10)
    private String abreviatura;

    @Column(name = "longitud")
    private Integer longitud;
}

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "producto")
public class Producto extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria_producto", nullable = false)
    private Categoria categoria;

    @Column(name = "codigo_barras", length = 30, nullable = false)
    private String codigoBarras;

    @Column(name = "n_producto", length = 50, nullable = false)
    private String nombre;

    @Column(name = "detalle", length = 100)
    private String descripcion;

    @Column(name = "p_compra", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioCompra;

    @Column(name = "p_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "p_mayoreo", precision = 10, scale = 2)
    private BigDecimal precioMayoreo;

    @Column(name = "stock_actual", precision = 12, scale = 3, nullable = false)
    private BigDecimal stock;

    @Column(name = "stock_minimo", precision = 12, scale = 3)
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo", precision = 12, scale = 3)
    private BigDecimal stockMaximo;

    @Column(name = "afecto_igv", length = 1, nullable = false)
    private String afectoIgv;

    @Column(name = "es_perecible", length = 1, nullable = false)
    private String esPerecible;

    @Column(name = "imagen", length = 200)
    private String imagen;
}

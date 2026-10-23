package com.bodeguita.bodeguita_backend.entity;

import java.math.BigDecimal;

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
@Table(name = "producto")
public class Producto extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_producto", nullable = false)
    private CategoriaProducto categoriaProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @Column(name = "codigo_barras", length = 30)
    private String codigoBarras;

    @Column(name = "n_producto", length = 50, nullable = false)
    private String nProducto;

    @Column(name = "detalle", length = 100)
    private String detalle;

    @Column(name = "p_compra", precision = 10, scale = 2, nullable = false)
    private BigDecimal pCompra;

    @Column(name = "p_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal pVenta;

    @Column(name = "p_mayoreo", precision = 10, scale = 2)
    private BigDecimal pMayoreo;

    @Column(name = "stock_actual", precision = 12, scale = 3, nullable = false)
    private BigDecimal stockActual;

    @Column(name = "stock_minimo", precision = 12, scale = 3, nullable = false)
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo", precision = 12, scale = 3)
    private BigDecimal stockMaximo;

    @Column(name = "afecto_igv", length = 1, nullable = false)
    private Boolean afectoIgv;

    @Column(name = "es_perecible", length = 1, nullable = false)
    private Boolean esPerecible;

    @Column(name = "imagen", length = 200)
    private String imagen;
}

package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    @Query("""
            SELECT producto FROM Producto producto
            JOIN FETCH producto.categoriaProducto
            LEFT JOIN FETCH producto.marca
            WHERE producto.idProducto = :idProducto
            """)
    Optional<Producto> findDetalle(@Param("idProducto") Long idProducto);

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsByCodigoBarrasAndIdProductoNot(String codigoBarras, Long idProducto);
}

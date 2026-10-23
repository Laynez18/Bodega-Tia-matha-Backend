package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.CategoriaProducto;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    @Query("""
            SELECT categoria FROM CategoriaProducto categoria
            WHERE categoria.estado = TRUE
            ORDER BY categoria.nCategoriaProducto ASC
            """)
    List<CategoriaProducto> findAllActivasOrdenadas();

    @Query("""
            SELECT categoria FROM CategoriaProducto categoria
            ORDER BY categoria.nCategoriaProducto ASC
            """)
    List<CategoriaProducto> findAllOrdenadas();

    Optional<CategoriaProducto> findByIdCategoriaProductoAndEstadoTrue(Long idCategoriaProducto);
}

package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.UnidadMedida;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    @Query("""
            SELECT unidad FROM UnidadMedida unidad
            WHERE unidad.estado = TRUE
            ORDER BY unidad.nUnidadMedida ASC
            """)
    List<UnidadMedida> findAllActivasOrdenadas();

    Optional<UnidadMedida> findByIdUnidadMedidaAndEstadoTrue(Long idUnidadMedida);
}

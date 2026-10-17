package com.bodeguita.bodeguita_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;

public interface TipoIdentidadRepository extends JpaRepository<TipoIdentidad, Long> {

    @Query("SELECT tipo FROM TipoIdentidad tipo WHERE tipo.estado = TRUE ORDER BY tipo.nTipoIdentidad ASC")
    List<TipoIdentidad> findAllActivosOrdenados();

    java.util.Optional<TipoIdentidad> findByIdTipoIdentidadAndEstadoTrue(Long idTipoIdentidad);
}

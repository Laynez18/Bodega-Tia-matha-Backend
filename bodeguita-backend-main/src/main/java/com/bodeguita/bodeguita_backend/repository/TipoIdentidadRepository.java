package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;

public interface TipoIdentidadRepository extends JpaRepository<TipoIdentidad, Long> {

    @Query("SELECT tipo FROM TipoIdentidad tipo WHERE tipo.estado = '1' ORDER BY tipo.nTipoIdentidad ASC")
    List<TipoIdentidad> findAllActivosOrdenados();

    @Query("SELECT t FROM TipoIdentidad t WHERE t.idTipoIdentidad = :idTipoIdentidad AND t.estado = '1'")
    Optional<TipoIdentidad> findByIdTipoIdentidadAndEstadoTrue(Long idTipoIdentidad);

    @Query("SELECT t FROM TipoIdentidad t WHERE t.idTipoIdentidad = :id AND t.estado = :estado")
    Optional<TipoIdentidad> findByIdAndEstado(Long id, String estado);
}

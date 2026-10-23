package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    @Query("""
            SELECT marca FROM Marca marca
            WHERE marca.estado = TRUE
            ORDER BY marca.nMarca ASC
            """)
    List<Marca> findAllActivasOrdenadas();

    Optional<Marca> findByIdMarcaAndEstadoTrue(Long idMarca);
}

package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    @Query("SELECT p FROM Persona p WHERE p.idPersona = :id AND p.estado = :estado")
    Optional<Persona> findByIdAndEstado(Long id, String estado);

    List<Persona> findByEstadoOrderByNombreAsc(String estado);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Persona p WHERE p.nDocumento = :nDocumento")
    boolean existsByNDocumento(@Param("nDocumento") String nDocumento);
}



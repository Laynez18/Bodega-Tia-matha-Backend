package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByIdAndEstado(Long id, String estado);

    List<Persona> findByEstadoOrderByNombreAsc(String estado);
}



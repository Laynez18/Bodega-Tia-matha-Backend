package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.ConceptoCaja;

public interface ConceptoCajaRepository extends JpaRepository<ConceptoCaja, Long> {
    Optional<ConceptoCaja> findByIdAndEstado(Long id, String estado);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}


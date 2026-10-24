package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.TipoMovimientoCaja;

public interface TipoMovimientoCajaRepository extends JpaRepository<TipoMovimientoCaja, Long> {
    Optional<TipoMovimientoCaja> findByIdAndEstado(Long id, String estado);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}


package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.TipoMovimientoInv;

public interface TipoMovimientoInvRepository extends JpaRepository<TipoMovimientoInv, Long> {
    Optional<TipoMovimientoInv> findByIdAndEstado(Long id, String estado);
}



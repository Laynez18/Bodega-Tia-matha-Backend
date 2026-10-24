package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.AperturaCaja;

public interface AperturaCajaRepository extends JpaRepository<AperturaCaja, Long> {
    Optional<AperturaCaja> findByIdAndEstado(Long id, String estado);
}



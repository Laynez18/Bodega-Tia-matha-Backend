package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Credito;

public interface CreditoRepository extends JpaRepository<Credito, Long> {
    Optional<Credito> findByIdAndEstado(Long id, String estado);
}



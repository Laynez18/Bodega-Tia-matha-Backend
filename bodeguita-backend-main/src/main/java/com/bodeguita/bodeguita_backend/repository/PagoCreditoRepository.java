package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.PagoCredito;

public interface PagoCreditoRepository extends JpaRepository<PagoCredito, Long> {
}

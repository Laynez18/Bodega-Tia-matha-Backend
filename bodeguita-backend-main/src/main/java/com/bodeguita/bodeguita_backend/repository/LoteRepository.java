package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Lote;

public interface LoteRepository extends JpaRepository<Lote, Long> {
}

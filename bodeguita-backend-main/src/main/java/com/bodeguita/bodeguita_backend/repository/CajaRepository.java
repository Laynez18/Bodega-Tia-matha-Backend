package com.bodeguita.bodeguita_backend.repository;

import com.bodeguita.bodeguita_backend.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CajaRepository extends JpaRepository<Caja, Long> {
}
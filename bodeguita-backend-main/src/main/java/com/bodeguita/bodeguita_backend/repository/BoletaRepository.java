package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
}

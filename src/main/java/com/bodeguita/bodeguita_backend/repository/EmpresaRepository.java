package com.bodeguita.bodeguita_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByRuc(String ruc);
}

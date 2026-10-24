package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.PagoCuenta;

public interface PagoCuentaRepository extends JpaRepository<PagoCuenta, Long> {
}

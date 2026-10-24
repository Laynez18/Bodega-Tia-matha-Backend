package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.PresentacionProducto;

public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {
    Optional<PresentacionProducto> findByIdAndEstado(Long id, String estado);
}



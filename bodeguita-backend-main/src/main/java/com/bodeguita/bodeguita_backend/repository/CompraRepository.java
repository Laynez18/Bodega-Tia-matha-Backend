package com.bodeguita.bodeguita_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    Optional<Compra> findByIdAndEstado(Long id, String estado);

    @Query("SELECT count(c), coalesce(sum(c.total), 0) FROM Compra c WHERE c.fCompra between :inicio and :fin and c.estado = '1'")
    List<Object[]> resumenPorFecha(LocalDateTime inicio, LocalDateTime fin);
}



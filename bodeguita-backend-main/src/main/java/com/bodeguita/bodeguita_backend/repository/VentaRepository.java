package com.bodeguita.bodeguita_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT count(v), coalesce(sum(v.total), 0) FROM Venta v WHERE v.fVenta between :inicio and :fin and v.estado = 'ACTIVO'")
    List<Object[]> resumenPorFecha(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT DATE(v.fVenta), count(v), sum(v.total) FROM Venta v WHERE v.fVenta between :inicio and :fin and v.estado = 'ACTIVO' GROUP BY DATE(v.fVenta) ORDER BY DATE(v.fVenta)")
    List<Object[]> ventasPorDia(LocalDateTime inicio, LocalDateTime fin);
}

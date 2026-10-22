package com.bodeguita.bodeguita_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodeguita.bodeguita_backend.dto.ResumenComprasDto;
import com.bodeguita.bodeguita_backend.dto.ResumenVentasDto;
import com.bodeguita.bodeguita_backend.dto.VentaPorDiaDto;
import com.bodeguita.bodeguita_backend.repository.CompraRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;

    public ReporteService(VentaRepository ventaRepository, CompraRepository compraRepository) {
        this.ventaRepository = ventaRepository;
        this.compraRepository = compraRepository;
    }

    public ResumenVentasDto resumenVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Object[]> ventas = ventaRepository.resumenPorFecha(fechaInicio, fechaFin);
        Object[] fila = ventas.isEmpty() ? new Object[] { 0L, BigDecimal.ZERO } : ventas.get(0);
        return new ResumenVentasDto(fechaInicio, fechaFin, (Long) fila[0], (BigDecimal) fila[1]);
    }

    public ResumenComprasDto resumenCompras(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Object[]> compras = compraRepository.resumenPorFecha(fechaInicio, fechaFin);
        Object[] fila = compras.isEmpty() ? new Object[] { 0L, BigDecimal.ZERO } : compras.get(0);
        return new ResumenComprasDto(fechaInicio, fechaFin, (Long) fila[0], (BigDecimal) fila[1]);
    }

    public List<VentaPorDiaDto> ventasPorDia(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.ventasPorDia(fechaInicio, fechaFin).stream()
                .map(fila -> new VentaPorDiaDto(String.valueOf(fila[0]), (Long) fila[1], (BigDecimal) fila[2]))
                .toList();
    }
}

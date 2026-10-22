package com.bodeguita.bodeguita_backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bodeguita.bodeguita_backend.dto.ResumenComprasDto;
import com.bodeguita.bodeguita_backend.dto.ResumenVentasDto;
import com.bodeguita.bodeguita_backend.dto.VentaPorDiaDto;
import com.bodeguita.bodeguita_backend.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas/resumen")
    public ResumenVentasDto resumenVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return reporteService.resumenVentas(fechaInicio, fechaFin);
    }

    @GetMapping("/compras/resumen")
    public ResumenComprasDto resumenCompras(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return reporteService.resumenCompras(fechaInicio, fechaFin);
    }

    @GetMapping("/ventas/por-dia")
    public List<VentaPorDiaDto> ventasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return reporteService.ventasPorDia(fechaInicio, fechaFin);
    }
}

package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CuentaCobrarDto;
import com.bodeguita.bodeguita_backend.dto.CuentaCobrarRequest;
import com.bodeguita.bodeguita_backend.entity.CuentaCobrar;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.Venta;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.CuentaCobrarRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CuentaCobrarService {

    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;

    public CuentaCobrarService(CuentaCobrarRepository cuentaCobrarRepository, VentaRepository ventaRepository,
            ClienteRepository clienteRepository) {
        this.cuentaCobrarRepository = cuentaCobrarRepository;
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public CuentaCobrarDto generar(Long idVenta, CuentaCobrarRequest request) {
        Venta venta = ventaRepository.findByIdAndEstado(idVenta, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada: " + idVenta + "."));
        Cliente cliente = clienteRepository.findByIdAndEstado(request.idCliente(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado: " + request.idCliente() + "."));
        CuentaCobrar cuenta = new CuentaCobrar();
        cuenta.setVenta(venta);
        cuenta.setCliente(cliente);
        cuenta.setMontoTotal(request.montoTotal());
        cuenta.setSaldo(request.montoTotal());
        cuenta.setFEmision(LocalDateTime.now());
        cuenta.setFVencimiento(request.fVencimiento());
        cuenta.setSituacion("P");
        cuenta = cuentaCobrarRepository.saveAndFlush(cuenta);
        return aDto(cuenta);
    }

    public CuentaCobrarDto obtener(Long id) {
        return aDto(cuentaCobrarRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cuenta por cobrar no encontrada: " + id + ".")));
    }

    private CuentaCobrarDto aDto(CuentaCobrar entidad) {
        return new CuentaCobrarDto(
                entidad.getId(),
                entidad.getVenta() != null ? entidad.getVenta().getId() : null,
                entidad.getCliente() != null ? entidad.getCliente().getId() : null,
                entidad.getMontoTotal(),
                entidad.getSaldo(),
                entidad.getFEmision(),
                entidad.getFVencimiento(),
                entidad.getSituacion(),
                entidad.getEstado()
        );
    }
}

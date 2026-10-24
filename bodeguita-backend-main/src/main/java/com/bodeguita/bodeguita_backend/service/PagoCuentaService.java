package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.PagoCuentaDto;
import com.bodeguita.bodeguita_backend.dto.PagoCuentaRequest;
import com.bodeguita.bodeguita_backend.entity.AperturaCaja;
import com.bodeguita.bodeguita_backend.entity.CuentaCobrar;
import com.bodeguita.bodeguita_backend.entity.MetodoPago;
import com.bodeguita.bodeguita_backend.entity.PagoCuenta;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.repository.AperturaCajaRepository;
import com.bodeguita.bodeguita_backend.repository.CuentaCobrarRepository;
import com.bodeguita.bodeguita_backend.repository.MetodoPagoRepository;
import com.bodeguita.bodeguita_backend.repository.PagoCuentaRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class PagoCuentaService {

    private final PagoCuentaRepository pagoCuentaRepository;
    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AperturaCajaRepository aperturaCajaRepository;

    public PagoCuentaService(PagoCuentaRepository pagoCuentaRepository, CuentaCobrarRepository cuentaCobrarRepository,
            MetodoPagoRepository metodoPagoRepository, UsuarioRepository usuarioRepository,
            AperturaCajaRepository aperturaCajaRepository) {
        this.pagoCuentaRepository = pagoCuentaRepository;
        this.cuentaCobrarRepository = cuentaCobrarRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.aperturaCajaRepository = aperturaCajaRepository;
    }

    @Transactional
    public PagoCuentaDto registrar(Long idCuenta, PagoCuentaRequest request) {
        CuentaCobrar cuenta = cuentaCobrarRepository.findById(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cuenta por cobrar no encontrada: " + idCuenta + "."));
        MetodoPago metodoPago = metodoPagoRepository.findByIdAndEstado(request.idMetodoPago(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + request.idMetodoPago() + "."));
        Usuario usuario = usuarioRepository.findByIdAndEstado(request.idUsuario(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + request.idUsuario() + "."));
        PagoCuenta pago = new PagoCuenta();
        pago.setCuenta(cuenta);
        pago.setMetodoPago(metodoPago);
        pago.setUsuario(usuario);
        pago.setMonto(request.monto());
        pago.setFPago(LocalDateTime.now());
        pago.setDocumento(request.documento());
        pago.setObservacion(request.observacion());
        if (request.idAperturaCaja() != null) {
            AperturaCaja aperturaCaja = aperturaCajaRepository.findByIdAndEstado(request.idAperturaCaja(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Apertura de caja no encontrada: " + request.idAperturaCaja() + "."));
            pago.setAperturaCaja(aperturaCaja);
        }
        pago = pagoCuentaRepository.saveAndFlush(pago);

        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(request.monto());
        cuenta.setSaldo(nuevoSaldo);
        cuenta.setSituacion(nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0 ? "C" : "P");
        cuentaCobrarRepository.save(cuenta);

        return aDto(pago);
    }

    public PagoCuentaDto obtener(Long id) {
        return aDto(pagoCuentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pago de cuenta no encontrado: " + id + ".")));
    }

    private PagoCuentaDto aDto(PagoCuenta entidad) {
        return new PagoCuentaDto(
                entidad.getId(),
                entidad.getCuenta() != null ? entidad.getCuenta().getId() : null,
                entidad.getMetodoPago() != null ? entidad.getMetodoPago().getId() : null,
                entidad.getUsuario() != null ? entidad.getUsuario().getId() : null,
                entidad.getAperturaCaja() != null ? entidad.getAperturaCaja().getId() : null,
                entidad.getMonto(),
                entidad.getFPago(),
                entidad.getDocumento(),
                entidad.getObservacion(),
                entidad.getEstado()
        );
    }
}

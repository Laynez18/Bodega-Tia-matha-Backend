package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CreditoDto;
import com.bodeguita.bodeguita_backend.dto.CreditoRequest;
import com.bodeguita.bodeguita_backend.dto.PagoCreditoDto;
import com.bodeguita.bodeguita_backend.dto.PagoCreditoRequest;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.Credito;
import com.bodeguita.bodeguita_backend.entity.MetodoPago;
import com.bodeguita.bodeguita_backend.entity.PagoCredito;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.CreditoRepository;
import com.bodeguita.bodeguita_backend.repository.MetodoPagoRepository;
import com.bodeguita.bodeguita_backend.repository.PagoCreditoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CreditoService {

    private final CreditoRepository creditoRepository;
    private final PagoCreditoRepository pagoCreditoRepository;
    private final ClienteRepository clienteRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public CreditoService(CreditoRepository creditoRepository, PagoCreditoRepository pagoCreditoRepository,
            ClienteRepository clienteRepository, MetodoPagoRepository metodoPagoRepository) {
        this.creditoRepository = creditoRepository;
        this.pagoCreditoRepository = pagoCreditoRepository;
        this.clienteRepository = clienteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    public List<CreditoDto> listar() {
        return creditoRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public CreditoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public CreditoDto crear(CreditoRequest request) {
        Cliente cliente = clienteRepository.findByIdAndEstado(request.idCliente(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado: " + request.idCliente() + "."));
        Credito credito = new Credito();
        credito.setCliente(cliente);
        credito.setNumeroDocumento(request.numeroDocumento());
        credito.setMonto(request.monto());
        credito.setSaldo(request.monto());
        credito.setfEmision(request.fEmision());
        credito.setfVencimiento(request.fVencimiento());
        credito.setEstado(request.estado());
        credito.setObservacion(request.observacion());
        credito = creditoRepository.saveAndFlush(credito);
        List<PagoCredito> pagos = request.pagos() == null ? List.of() : request.pagos().stream().map(p -> {
            MetodoPago mp = null;
            if (p.idMetodoPago() != null) {
                mp = metodoPagoRepository.findByIdAndEstado(p.idMetodoPago(), "1")
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + p.idMetodoPago() + "."));
            }
            PagoCredito pago = new PagoCredito();
            pago.setCredito(credito);
            pago.setMonto(p.monto());
            pago.setfPago(p.fPago());
            pago.setMetodoPago(mp);
            pago.setNumeroOperacion(p.numeroOperacion());
            pago.setObservacion(p.observacion());
            return pago;
        }).collect(Collectors.toList());
        for (PagoCredito pago : pagos) {
            pagoCreditoRepository.save(pago);
            credito.setSaldo(credito.getSaldo().subtract(pago.getMonto()));
        }
        return obtener(credito.getId());
    }

    @Transactional
    public CreditoDto registrarPago(Long idCredito, PagoCreditoRequest request) {
        Credito credito = buscarActivo(idCredito);
        MetodoPago mp = null;
        if (request.idMetodoPago() != null) {
            mp = metodoPagoRepository.findByIdAndEstado(request.idMetodoPago(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + request.idMetodoPago() + "."));
        }
        PagoCredito pago = new PagoCredito();
        pago.setCredito(credito);
        pago.setMonto(request.monto());
        pago.setfPago(request.fPago());
        pago.setMetodoPago(mp);
        pago.setNumeroOperacion(request.numeroOperacion());
        pago.setObservacion(request.observacion());
        pagoCreditoRepository.saveAndFlush(pago);
        credito.setSaldo(credito.getSaldo().subtract(request.monto()));
        return obtener(credito.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Credito buscarActivo(Long id) {
        return creditoRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Credito no encontrado: " + id + "."));
    }

    private CreditoDto aDto(Credito entidad) {
        List<PagoCreditoDto> pagos = entidad.getPagos().stream().map(p -> new PagoCreditoDto(
                p.getId(),
                p.getCredito().getId(),
                p.getMonto(),
                p.getfPago(),
                p.getMetodoPago() == null ? null : p.getMetodoPago().getId(),
                p.getMetodoPago() == null ? null : p.getMetodoPago().getNombre(),
                p.getNumeroOperacion(),
                p.getObservacion()
        )).collect(Collectors.toList());
        return new CreditoDto(
                entidad.getId(),
                entidad.getCliente().getId(),
                entidad.getCliente().getPersona().getNombre(),
                entidad.getNumeroDocumento(),
                entidad.getMonto(),
                entidad.getSaldo(),
                entidad.getfEmision(),
                entidad.getfVencimiento(),
                entidad.getEstado(),
                entidad.getObservacion(),
                pagos
        );
    }
}




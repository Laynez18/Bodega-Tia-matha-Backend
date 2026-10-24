package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.MovimientoCajaDto;
import com.bodeguita.bodeguita_backend.dto.MovimientoCajaRequest;
import com.bodeguita.bodeguita_backend.entity.AperturaCaja;
import com.bodeguita.bodeguita_backend.entity.ConceptoCaja;
import com.bodeguita.bodeguita_backend.entity.MetodoPago;
import com.bodeguita.bodeguita_backend.entity.MovimientoCaja;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoCaja;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.repository.AperturaCajaRepository;
import com.bodeguita.bodeguita_backend.repository.ConceptoCajaRepository;
import com.bodeguita.bodeguita_backend.repository.MetodoPagoRepository;
import com.bodeguita.bodeguita_backend.repository.MovimientoCajaRepository;
import com.bodeguita.bodeguita_backend.repository.TipoMovimientoCajaRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MovimientoCajaService {

    private final MovimientoCajaRepository movimientoCajaRepository;
    private final AperturaCajaRepository aperturaCajaRepository;
    private final TipoMovimientoCajaRepository tipoMovimientoCajaRepository;
    private final ConceptoCajaRepository conceptoCajaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoCajaService(MovimientoCajaRepository movimientoCajaRepository,
            AperturaCajaRepository aperturaCajaRepository, TipoMovimientoCajaRepository tipoMovimientoCajaRepository,
            ConceptoCajaRepository conceptoCajaRepository, MetodoPagoRepository metodoPagoRepository,
            UsuarioRepository usuarioRepository) {
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.aperturaCajaRepository = aperturaCajaRepository;
        this.tipoMovimientoCajaRepository = tipoMovimientoCajaRepository;
        this.conceptoCajaRepository = conceptoCajaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MovimientoCajaDto> listar() {
        return movimientoCajaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public MovimientoCajaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public MovimientoCajaDto crear(MovimientoCajaRequest request) {
        AperturaCaja aperturaCaja = aperturaCajaRepository.findByIdAndEstado(request.idAperturaCaja(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Apertura de caja no encontrada: " + request.idAperturaCaja() + "."));
        TipoMovimientoCaja tipoMovimiento = tipoMovimientoCajaRepository.findByIdAndEstado(request.idTipoMovimiento(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento no encontrado: " + request.idTipoMovimiento() + "."));
        ConceptoCaja concepto = conceptoCajaRepository.findByIdAndEstado(request.idConcepto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Concepto de caja no encontrado: " + request.idConcepto() + "."));
        MetodoPago metodoPago = metodoPagoRepository.findByIdAndEstado(request.idMetodoPago(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + request.idMetodoPago() + "."));
        Usuario usuario = usuarioRepository.findByIdAndEstado(request.idUsuario(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + request.idUsuario() + "."));
        MovimientoCaja entidad = new MovimientoCaja();
        entidad.setAperturaCaja(aperturaCaja);
        entidad.setTipoMovimiento(tipoMovimiento);
        entidad.setConcepto(concepto);
        entidad.setMetodoPago(metodoPago);
        entidad.setUsuario(usuario);
        entidad.setIdCompra(request.idCompra());
        entidad.setIdVenta(request.idVenta());
        entidad.setNumeroOperacion(request.numeroOperacion());
        entidad.setDocumento(request.documento());
        entidad.setDescripcion(request.descripcion());
        entidad.setMonto(request.monto());
        entidad.setAfectaEfectivo(request.afectaEfectivo());
        entidad.setfMovimiento(request.fMovimiento());
        entidad = movimientoCajaRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private MovimientoCaja buscarActivo(Long id) {
        return movimientoCajaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movimiento de caja no encontrado: " + id + "."));
    }

    private MovimientoCajaDto aDto(MovimientoCaja entidad) {
        return new MovimientoCajaDto(
                entidad.getId(),
                entidad.getAperturaCaja().getId(),
                entidad.getTipoMovimiento().getId(),
                entidad.getTipoMovimiento().getNombre(),
                entidad.getConcepto().getId(),
                entidad.getConcepto().getNombre(),
                entidad.getMetodoPago().getId(),
                entidad.getMetodoPago().getNombre(),
                entidad.getUsuario().getId(),
                entidad.getUsuario().getLogeo(),
                entidad.getIdCompra(),
                entidad.getIdVenta(),
                entidad.getNumeroOperacion(),
                entidad.getDocumento(),
                entidad.getDescripcion(),
                entidad.getMonto(),
                entidad.getAfectaEfectivo(),
                entidad.getfMovimiento()
        );
    }
}




package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.AperturaCajaCierreRequest;
import com.bodeguita.bodeguita_backend.dto.AperturaCajaDto;
import com.bodeguita.bodeguita_backend.dto.AperturaCajaRequest;
import com.bodeguita.bodeguita_backend.entity.AperturaCaja;
import com.bodeguita.bodeguita_backend.entity.Caja;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.repository.AperturaCajaRepository;
import com.bodeguita.bodeguita_backend.repository.CajaRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class AperturaCajaService {

    private final AperturaCajaRepository aperturaCajaRepository;
    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;

    public AperturaCajaService(AperturaCajaRepository aperturaCajaRepository, CajaRepository cajaRepository,
            UsuarioRepository usuarioRepository) {
        this.aperturaCajaRepository = aperturaCajaRepository;
        this.cajaRepository = cajaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AperturaCajaDto> listar() {
        return aperturaCajaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public AperturaCajaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public AperturaCajaDto crear(AperturaCajaRequest request) {
        Caja caja = cajaRepository.findByIdAndEstado(request.idCaja(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Caja no encontrada: " + request.idCaja() + "."));
        Usuario usuario = usuarioRepository.findByIdAndEstado(request.idUsuario(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + request.idUsuario() + "."));
        AperturaCaja entidad = new AperturaCaja();
        entidad.setCaja(caja);
        entidad.setUsuario(usuario);
        entidad.setNumeroTurno(request.numeroTurno());
        entidad.setFApertura(request.fApertura());
        entidad.setMontoInicial(request.montoInicial());
        entidad.setObservacion(request.observacion());
        entidad = aperturaCajaRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    @Transactional
    public AperturaCajaDto cerrar(Long id, AperturaCajaCierreRequest request) {
        AperturaCaja entidad = buscarActivo(id);
        entidad.setMontoDeclarado(request.montoDeclarado());
        entidad.setObservacion(request.observacion());
        entidad.setSituacion(request.situacion());
        entidad.setFCierre(java.time.LocalDateTime.now());
        aperturaCajaRepository.save(entidad);
        return obtener(id);
    }


    private AperturaCaja buscarActivo(Long id) {
        return aperturaCajaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Apertura de caja no encontrada: " + id + "."));
    }

    private AperturaCajaDto aDto(AperturaCaja entidad) {
        return new AperturaCajaDto(
                entidad.getId(),
                entidad.getCaja().getId(),
                entidad.getCaja().getNombre(),
                entidad.getUsuario().getId(),
                entidad.getUsuario().getLogeo(),
                entidad.getNumeroTurno(),
                entidad.getFApertura(),
                entidad.getFCierre(),
                entidad.getMontoInicial(),
                entidad.getTotalIngresos(),
                entidad.getTotalEgresos(),
                entidad.getMontoSistema(),
                entidad.getMontoDeclarado(),
                entidad.getDiferencia(),
                entidad.getSituacion(),
                entidad.getObservacion()
        );
    }
}




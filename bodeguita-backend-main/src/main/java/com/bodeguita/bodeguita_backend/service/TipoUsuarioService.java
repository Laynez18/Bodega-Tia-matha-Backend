package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.TipoUsuarioDto;
import com.bodeguita.bodeguita_backend.entity.TipoUsuario;
import com.bodeguita.bodeguita_backend.repository.TipoUsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public TipoUsuarioService(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public List<TipoUsuarioDto> listar() {
        return tipoUsuarioRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public TipoUsuarioDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public TipoUsuarioDto crear(TipoUsuario entidad) {
        return aDto(tipoUsuarioRepository.save(entidad));
    }

    @Transactional
    public TipoUsuarioDto actualizar(Long id, TipoUsuario entidad) {
        TipoUsuario existente = buscarActivo(id);
        existente.setnTipoUsuario(entidad.getnTipoUsuario());
        return aDto(tipoUsuarioRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private TipoUsuario buscarActivo(Long id) {
        return tipoUsuarioRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de usuario no encontrado: " + id + "."));
    }

    private TipoUsuarioDto aDto(TipoUsuario entidad) {
        return new TipoUsuarioDto(entidad.getIdTipoUsuario(), entidad.getnTipoUsuario());
    }
}

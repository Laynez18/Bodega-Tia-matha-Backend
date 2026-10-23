package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CategoriaProductoDto;
import com.bodeguita.bodeguita_backend.dto.CategoriaProductoRequest;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.entity.CategoriaProducto;
import com.bodeguita.bodeguita_backend.repository.CategoriaProductoRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final AuditoriaService auditoriaService;

    public CategoriaProductoService(CategoriaProductoRepository categoriaProductoRepository,
            AuditoriaService auditoriaService) {
        this.categoriaProductoRepository = categoriaProductoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<CategoriaProductoDto> listar() {
        return categoriaProductoRepository.findAllOrdenadas().stream()
                .map(this::aDto)
                .toList();
    }

    public CategoriaProductoDto obtener(Long idCategoriaProducto) {
        return aDto(buscar(idCategoriaProducto));
    }

    @Transactional
    public CategoriaProductoDto crear(CategoriaProductoRequest request) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNCategoriaProducto(request.nCategoriaProducto().trim());
        categoria.setDescripcion(normalizarOpcional(request.descripcion()));
        if (categoria.getEstado() == null) {
            categoria.setEstado(Boolean.TRUE);
        }
        categoriaProductoRepository.saveAndFlush(categoria);
        CategoriaProductoDto dto = aDto(categoria);
        try {
            auditoriaService.registrar("categoria_producto", "INSERT", categoria.getIdCategoriaProducto(),
                    null, resumen(categoria));
        } catch (Exception ex) {
            // No bloquear
        }
        return dto;
    }

    @Transactional
    public CategoriaProductoDto actualizar(Long idCategoriaProducto, CategoriaProductoRequest request) {
        CategoriaProducto categoria = buscar(idCategoriaProducto);
        String anterior = resumen(categoria);
        categoria.setNCategoriaProducto(request.nCategoriaProducto().trim());
        categoria.setDescripcion(normalizarOpcional(request.descripcion()));
        categoriaProductoRepository.saveAndFlush(categoria);
        CategoriaProductoDto dto = aDto(categoria);
        try {
            auditoriaService.registrar("categoria_producto", "UPDATE", categoria.getIdCategoriaProducto(),
                    anterior, resumen(categoria));
        } catch (Exception ex) {
            // No bloquear
        }
        return dto;
    }

    @Transactional
    public CategoriaProductoDto cambiarEstado(Long idCategoriaProducto, EstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoriaProducto)
                .orElseThrow(() -> noEncontrado(idCategoriaProducto));
        String anterior = resumen(categoria);
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(categoria.getEstado()) == nuevoEstado) {
            return aDto(categoria);
        }
        categoria.setEstado(nuevoEstado);
        categoriaProductoRepository.saveAndFlush(categoria);
        try {
            auditoriaService.registrar("categoria_producto", nuevoEstado ? "ACTIVAR" : "DESACTIVAR",
                    idCategoriaProducto, anterior, resumen(categoria));
        } catch (Exception ex) {
            // No bloquear
        }
        return aDto(categoria);
    }

    @Transactional
    public void desactivar(Long idCategoriaProducto) {
        CategoriaProducto categoria = buscar(idCategoriaProducto);
        String anterior = resumen(categoria);
        categoria.setEstado(Boolean.FALSE);
        categoriaProductoRepository.saveAndFlush(categoria);
        try {
            auditoriaService.registrar("categoria_producto", "DESACTIVAR", idCategoriaProducto, anterior,
                    resumen(categoria));
        } catch (Exception ex) {
            // No bloquear
        }
    }

    private CategoriaProducto buscar(Long idCategoriaProducto) {
        return categoriaProductoRepository.findById(idCategoriaProducto)
                .orElseThrow(() -> noEncontrado(idCategoriaProducto));
    }

    private CategoriaProductoDto aDto(CategoriaProducto categoria) {
        return new CategoriaProductoDto(
                categoria.getIdCategoriaProducto(),
                categoria.getNCategoriaProducto(),
                categoria.getDescripcion(),
                categoria.getEstado());
    }

    private String resumen(CategoriaProducto categoria) {
        return "nombre=" + (categoria.getNCategoriaProducto() == null ? "" : categoria.getNCategoriaProducto())
                + "; descripcion=" + (categoria.getDescripcion() == null ? "" : categoria.getDescripcion())
                + "; estado=" + (Boolean.TRUE.equals(categoria.getEstado()) ? "1" : "0");
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private ResponseStatusException noEncontrado(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada: " + id + ".");
    }
}
package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioDto;
import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioRequest;
import com.bodeguita.bodeguita_backend.entity.Almacen;
import com.bodeguita.bodeguita_backend.entity.Lote;
import com.bodeguita.bodeguita_backend.entity.MovimientoInventario;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoInv;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.entity.Venta;
import com.bodeguita.bodeguita_backend.entity.Compra;
import com.bodeguita.bodeguita_backend.repository.AlmacenRepository;
import com.bodeguita.bodeguita_backend.repository.LoteRepository;
import com.bodeguita.bodeguita_backend.repository.MovimientoInventarioRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.TipoMovimientoInvRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;
import com.bodeguita.bodeguita_backend.repository.CompraRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final TipoMovimientoInvRepository tipoMovimientoInvRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;

    public MovimientoInventarioService(
            MovimientoInventarioRepository repository,
            ProductoRepository productoRepository,
            AlmacenRepository almacenRepository,
            TipoMovimientoInvRepository tipoMovimientoInvRepository,
            LoteRepository loteRepository,
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository,
            CompraRepository compraRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.tipoMovimientoInvRepository = tipoMovimientoInvRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.compraRepository = compraRepository;
    }

    public List<MovimientoInventarioDto> listar() {
        return repository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public MovimientoInventarioDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public MovimientoInventarioDto crear(MovimientoInventarioRequest request) {
        Producto producto = productoRepository.findByIdAndEstado(request.idProducto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + request.idProducto() + "."));
        Almacen almacen = almacenRepository.findByIdAndEstado(request.idAlmacen(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Almacen no encontrado: " + request.idAlmacen() + "."));
        TipoMovimientoInv tipoMovimientoInv = tipoMovimientoInvRepository.findByIdAndEstado(request.idTipoMovimientoInv(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento no encontrado: " + request.idTipoMovimientoInv() + "."));
        Usuario usuario = usuarioRepository.findByIdAndEstado(request.idUsuario(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + request.idUsuario() + "."));
        MovimientoInventario entidad = new MovimientoInventario();
        entidad.setProducto(producto);
        entidad.setAlmacen(almacen);
        entidad.setTipoMovimientoInv(tipoMovimientoInv);
        entidad.setCantidad(request.cantidad());
        entidad.setStockAnterior(request.stockAnterior());
        entidad.setStockNuevo(request.stockNuevo());
        entidad.setFMovimiento(request.fMovimiento());
        entidad.setDocumento(request.documento());
        entidad.setObservacion(request.observacion());
        entidad.setCostoUnitario(request.costoUnitario());
        if (request.idLote() != null) {
            Lote lote = loteRepository.findByIdAndEstado(request.idLote(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lote no encontrado: " + request.idLote() + "."));
            entidad.setLote(lote);
        }
        if (request.idVenta() != null) {
            Venta venta = ventaRepository.findByIdAndEstado(request.idVenta(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada: " + request.idVenta() + "."));
            entidad.setVenta(venta);
        }
        if (request.idCompra() != null) {
            Compra compra = compraRepository.findByIdAndEstado(request.idCompra(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Compra no encontrada: " + request.idCompra() + "."));
            entidad.setCompra(compra);
        }
        entidad = repository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private MovimientoInventario buscarActivo(Long id) {
        return repository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movimiento de inventario no encontrado: " + id + "."));
    }

    private MovimientoInventarioDto aDto(MovimientoInventario entidad) {
        return new MovimientoInventarioDto(
                entidad.getId(),
                entidad.getProducto().getId(),
                entidad.getProducto().getNombre(),
                entidad.getTipoMovimientoInv().getNombre(),
                entidad.getCantidad(),
                entidad.getStockAnterior(),
                entidad.getStockNuevo(),
                entidad.getFMovimiento(),
                entidad.getDocumento(),
                entidad.getObservacion(),
                entidad.getCostoUnitario()
        );
    }
}

package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CompraDto;
import com.bodeguita.bodeguita_backend.dto.CompraRequest;
import com.bodeguita.bodeguita_backend.dto.DetalleCompraDto;
import com.bodeguita.bodeguita_backend.dto.DetalleCompraRequest;
import com.bodeguita.bodeguita_backend.entity.Compra;
import com.bodeguita.bodeguita_backend.entity.DetalleCompra;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.entity.Proveedor;
import com.bodeguita.bodeguita_backend.repository.CompraRepository;
import com.bodeguita.bodeguita_backend.repository.DetalleCompraRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.ProveedorRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    public CompraService(CompraRepository compraRepository, DetalleCompraRepository detalleCompraRepository,
            ProveedorRepository proveedorRepository, ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
    }

    public List<CompraDto> listar() {
        return compraRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public CompraDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public CompraDto crear(CompraRequest request) {
        Proveedor proveedor = proveedorRepository.findByIdAndEstado(request.idProveedor(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado: " + request.idProveedor() + "."));
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setDocumento(request.documento());
        compra.setFCompra(request.fCompra());
        compra.setEstado(request.estado());
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        List<DetalleCompra> detalles = request.detalles().stream().map(d -> {
            Producto producto = productoRepository.findByIdAndEstado(d.idProducto(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + d.idProducto() + "."));
            DetalleCompra dc = new DetalleCompra();
            dc.setProducto(producto);
            dc.setCantidad(d.cantidad());
            dc.setPrecioUnitario(d.precioUnitario());
            dc.setSubtotal(d.precioUnitario().multiply(BigDecimal.valueOf(d.cantidad())));
            return dc;
        }).collect(Collectors.toList());
        for (DetalleCompra dc : detalles) {
            subtotal = subtotal.add(dc.getSubtotal());
        }
        igv = subtotal.multiply(new BigDecimal("0.18"));
        compra.setSubtotal(subtotal);
        compra.setIgv(igv);
        compra.setTotal(subtotal.add(igv));
        compra = compraRepository.saveAndFlush(compra);
        for (DetalleCompra dc : detalles) {
            dc.setCompra(compra);
            detalleCompraRepository.save(dc);
            Producto producto = dc.getProducto();
            producto.setStock(producto.getStock() + dc.getCantidad());
            productoRepository.save(producto);
        }
        return obtener(compra.getId());
    }

    @Transactional
    public CompraDto actualizar(Long id, CompraRequest request) {
        Compra entidad = buscarActivo(id);
        Proveedor proveedor = proveedorRepository.findByIdAndEstado(request.idProveedor(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado: " + request.idProveedor() + "."));
        entidad.setProveedor(proveedor);
        entidad.setDocumento(request.documento());
        entidad.setFCompra(request.fCompra());
        entidad.setEstado(request.estado());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Compra buscarActivo(Long id) {
        return compraRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Compra no encontrada: " + id + "."));
    }

    private CompraDto aDto(Compra entidad) {
        List<DetalleCompraDto> detalles = entidad.getDetalles().stream().map(dc -> new DetalleCompraDto(
                dc.getId(),
                dc.getProducto().getId(),
                dc.getProducto().getNombre(),
                dc.getCantidad(),
                dc.getPrecioUnitario(),
                dc.getSubtotal()
        )).collect(Collectors.toList());
        return new CompraDto(
                entidad.getId(),
                entidad.getProveedor().getId(),
                entidad.getProveedor().getPersona().getNombre(),
                entidad.getDocumento(),
                entidad.getFCompra(),
                entidad.getSubtotal(),
                entidad.getIgv(),
                entidad.getTotal(),
                entidad.getEstado(),
                detalles
        );
    }
}

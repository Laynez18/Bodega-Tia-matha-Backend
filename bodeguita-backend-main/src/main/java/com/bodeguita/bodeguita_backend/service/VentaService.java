package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.DetalleVentaDto;
import com.bodeguita.bodeguita_backend.dto.DetalleVentaRequest;
import com.bodeguita.bodeguita_backend.dto.VentaDto;
import com.bodeguita.bodeguita_backend.dto.VentaRequest;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.DetalleVenta;
import com.bodeguita.bodeguita_backend.entity.MetodoPago;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.entity.Venta;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.DetalleVentaRepository;
import com.bodeguita.bodeguita_backend.repository.MetodoPagoRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public VentaService(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository,
            ClienteRepository clienteRepository, ProductoRepository productoRepository, MetodoPagoRepository metodoPagoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    public List<VentaDto> listar() {
        return ventaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public VentaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public VentaDto crear(VentaRequest request) {
        Cliente cliente = clienteRepository.findByIdAndEstado(request.idCliente(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado: " + request.idCliente() + "."));
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFVenta(request.fVenta());
        MetodoPago metodoPago = null;
        if (request.idMetodoPago() != null) {
            metodoPago = metodoPagoRepository.findByIdAndEstado(request.idMetodoPago(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + request.idMetodoPago() + "."));
        }
        venta.setMetodoPago(metodoPago);
        venta.setEstado(request.estado());
        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetalleVenta> detalles = request.detalles().stream().map(d -> {
            Producto producto = productoRepository.findByIdAndEstado(d.idProducto(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + d.idProducto() + "."));
            BigDecimal cantidad = BigDecimal.valueOf(d.cantidad());
            if (producto.getStock() == null || producto.getStock().compareTo(cantidad) < 0) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para el producto: " + producto.getNombre() + " (disponible: " + producto.getStock() + ")");
            }
            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(producto);
            dv.setCantidad(BigDecimal.valueOf(d.cantidad()));
            dv.setPrecioUnitario(d.precioUnitario());
            dv.setSubtotal(d.precioUnitario().multiply(cantidad));
            return dv;
        }).collect(Collectors.toList());
        for (DetalleVenta dv : detalles) {
            subtotal = subtotal.add(dv.getSubtotal());
        }
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal descuento = BigDecimal.ZERO;
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setDescuento(descuento);
        venta.setTotal(subtotal.add(igv).subtract(descuento));
        venta = ventaRepository.saveAndFlush(venta);
        for (DetalleVenta dv : detalles) {
            dv.setVenta(venta);
            detalleVentaRepository.save(dv);
            Producto producto = dv.getProducto();
            producto.setStock(producto.getStock().subtract(dv.getCantidad()));
            productoRepository.save(producto);
        }
        return obtener(venta.getId());
    }

    @Transactional
    public VentaDto actualizar(Long id, VentaRequest request) {
        Venta entidad = buscarActivo(id);
        Cliente cliente = clienteRepository.findByIdAndEstado(request.idCliente(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado: " + request.idCliente() + "."));
        entidad.setCliente(cliente);
        entidad.setFVenta(request.fVenta());
        if (request.idMetodoPago() != null) {
            MetodoPago metodoPago = metodoPagoRepository.findByIdAndEstado(request.idMetodoPago(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + request.idMetodoPago() + "."));
            entidad.setMetodoPago(metodoPago);
        }
        entidad.setEstado(request.estado());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Venta buscarActivo(Long id) {
        return ventaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada: " + id + "."));
    }

    private VentaDto aDto(Venta entidad) {
        List<DetalleVentaDto> detalles = entidad.getDetalles().stream().map(dv -> new DetalleVentaDto(
                dv.getId(),
                dv.getProducto().getId(),
                dv.getProducto().getNombre(),
                dv.getCantidad().intValue(),
                dv.getPrecioUnitario(),
                dv.getSubtotal()
        )).collect(Collectors.toList());
        return new VentaDto(
                entidad.getId(),
                entidad.getCliente().getId(),
                entidad.getCliente().getPersona().getNombre(),
                entidad.getFVenta(),
                entidad.getSubtotal(),
                entidad.getIgv(),
                entidad.getDescuento(),
                entidad.getTotal(),
                entidad.getMetodoPago() == null ? null : entidad.getMetodoPago().getId(),
                entidad.getMetodoPago() == null ? null : entidad.getMetodoPago().getNombre(),
                entidad.getEstado(),
                detalles
        );
    }
}

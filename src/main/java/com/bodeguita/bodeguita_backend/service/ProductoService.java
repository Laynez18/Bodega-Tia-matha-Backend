package com.bodeguita.bodeguita_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CategoriaProductoDto;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.MarcaDto;
import com.bodeguita.bodeguita_backend.dto.ProductoDto;
import com.bodeguita.bodeguita_backend.dto.ProductoRequest;
import com.bodeguita.bodeguita_backend.dto.UnidadMedidaDto;
import com.bodeguita.bodeguita_backend.entity.CategoriaProducto;
import com.bodeguita.bodeguita_backend.entity.Marca;
import com.bodeguita.bodeguita_backend.entity.PresentacionProducto;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.entity.UnidadMedida;
import com.bodeguita.bodeguita_backend.repository.CategoriaProductoRepository;
import com.bodeguita.bodeguita_backend.repository.MarcaRepository;
import com.bodeguita.bodeguita_backend.repository.PresentacionProductoRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.UnidadMedidaRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private static final int TAMANO_MAXIMO = 100;
    private static final int ESCALA_PRECIO = 2;
    private static final int ESCALA_STOCK = 3;

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final MarcaRepository marcaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final PresentacionProductoRepository presentacionProductoRepository;
    private final AuditoriaService auditoriaService;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaProductoRepository categoriaProductoRepository,
            MarcaRepository marcaRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            PresentacionProductoRepository presentacionProductoRepository,
            AuditoriaService auditoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
        this.marcaRepository = marcaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.presentacionProductoRepository = presentacionProductoRepository;
        this.auditoriaService = auditoriaService;
    }

    public Page<ProductoDto> listar(
            String search,
            Long idCategoria,
            Long idMarca,
            String estado,
            boolean stockBajo,
            int page,
            int size) {

        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), TAMANO_MAXIMO);
        Pageable pageable = PageRequest.of(
                pagina, tamano, Sort.by(Sort.Direction.ASC, "nProducto", "idProducto"));

        return productoRepository.findAll(construirSpec(search, idCategoria, idMarca, estado, stockBajo), pageable)
                .map(this::aDto);
    }

    public ProductoDto obtener(Long idProducto) {
        return aDto(buscar(idProducto));
    }

    public List<CategoriaProductoDto> listarCategorias() {
        return categoriaProductoRepository.findAllOrdenadas().stream()
                .map(categoria -> new CategoriaProductoDto(
                        categoria.getIdCategoriaProducto(),
                        categoria.getNCategoriaProducto(),
                        categoria.getDescripcion(),
                        categoria.getEstado()))
                .toList();
    }

    public List<MarcaDto> listarMarcas() {
        return marcaRepository.findAllActivasOrdenadas().stream()
                .map(marca -> new MarcaDto(marca.getIdMarca(), marca.getNMarca()))
                .toList();
    }

    public List<UnidadMedidaDto> listarUnidades() {
        return unidadMedidaRepository.findAllActivasOrdenadas().stream()
                .map(unidad -> new UnidadMedidaDto(
                        unidad.getIdUnidadMedida(),
                        unidad.getNUnidadMedida(),
                        unidad.getAbreviatura()))
                .toList();
    }

    @Transactional
    public ProductoDto crear(ProductoRequest request) {
        CategoriaProducto categoria = buscarCategoriaActiva(request.idCategoriaProducto());
        Marca marca = buscarMarcaActiva(request.idMarca());
        UnidadMedida unidad = buscarUnidadActiva(request.idUnidadMedidaBase());
        String codigo = normalizarCodigo(request.codigoBarras());
        if (codigo != null && productoRepository.existsByCodigoBarras(codigo)) {
            throw conflicto("El codigo de barras ya esta registrado.");
        }
        validarStockMaximo(request.stockMinimo(), request.stockMaximo());

        Producto producto = new Producto();
        aplicar(producto, request, categoria, marca, codigo);
        producto.setStockActual(escalaStock(request.stockActual()));
        producto.setEstado(Boolean.TRUE);
        productoRepository.saveAndFlush(producto);

        asegurarPresentacionBase(producto, unidad, request.factorConversionBase(), Boolean.TRUE);

        ProductoDto creado = aDto(producto);
        auditoriaService.registrar("producto", "INSERT", producto.getIdProducto(), null, resumen(creado));
        return creado;
    }

    @Transactional
    public ProductoDto actualizar(Long idProducto, ProductoRequest request) {
        Producto producto = buscar(idProducto);
        ProductoDto anterior = aDto(producto);

        CategoriaProducto categoria = buscarCategoriaActiva(request.idCategoriaProducto());
        Marca marca = buscarMarcaActiva(request.idMarca());
        UnidadMedida unidad = buscarUnidadActiva(request.idUnidadMedidaBase());
        String codigo = normalizarCodigo(request.codigoBarras());
        if (codigo != null && productoRepository.existsByCodigoBarrasAndIdProductoNot(codigo, idProducto)) {
            throw conflicto("El codigo de barras ya esta registrado.");
        }
        validarStockMaximo(request.stockMinimo(), request.stockMaximo());

        aplicar(producto, request, categoria, marca, codigo);

        PresentacionProducto base = presentacionProductoRepository
                .findFirstByProducto_IdProductoAndEsUnidadBaseTrueAndEstadoTrue(idProducto)
                .orElse(null);
        if (base == null) {
            asegurarPresentacionBase(
                    producto,
                    unidad,
                    request.factorConversionBase(),
                    Boolean.TRUE.equals(producto.getEstado()));
        } else {
            if (!base.getUnidadMedida().getIdUnidadMedida().equals(unidad.getIdUnidadMedida())
                    && presentacionProductoRepository
                            .existsByProducto_IdProductoAndUnidadMedida_IdUnidadMedidaAndIdPresentacionProductoNot(
                                    idProducto,
                                    unidad.getIdUnidadMedida(),
                                    base.getIdPresentacionProducto())) {
                throw conflicto("Ya existe una presentacion con la unidad de medida seleccionada.");
            }
            base.setUnidadMedida(unidad);
            base.setFactorConversion(escalaFactor(request.factorConversionBase()));
            base.setEsUnidadBase(Boolean.TRUE);
            presentacionProductoRepository.save(base);
        }

        productoRepository.saveAndFlush(producto);
        ProductoDto actualizado = aDto(producto);
        auditoriaService.registrar(
                "producto", "UPDATE", idProducto, resumen(anterior), resumen(actualizado));
        return actualizado;
    }

    @Transactional
    public ProductoDto cambiarEstado(Long idProducto, EstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }

        Producto producto = buscar(idProducto);
        ProductoDto anterior = aDto(producto);
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(producto.getEstado()) == nuevoEstado) {
            return anterior;
        }
        if (nuevoEstado) {
            if (!Boolean.TRUE.equals(producto.getCategoriaProducto().getEstado())) {
                throw conflicto("No se puede activar el producto porque su categoria esta inactiva.");
            }
            if (producto.getMarca() != null && !Boolean.TRUE.equals(producto.getMarca().getEstado())) {
                throw conflicto("No se puede activar el producto porque su marca esta inactiva.");
            }
        }

        producto.setEstado(nuevoEstado);
        productoRepository.saveAndFlush(producto);
        ProductoDto actualizado = aDto(producto);
        auditoriaService.registrar(
                "producto",
                nuevoEstado ? "ACTIVAR" : "DESACTIVAR",
                idProducto,
                resumen(anterior),
                resumen(actualizado));
        return actualizado;
    }

    @Transactional
    public void desactivar(Long idProducto) {
        Producto producto = buscar(idProducto);
        ProductoDto anterior = aDto(producto);
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            return;
        }
        producto.setEstado(Boolean.FALSE);
        productoRepository.saveAndFlush(producto);
        auditoriaService.registrar(
                "producto", "DESACTIVAR", idProducto, resumen(anterior), resumen(aDto(producto)));
    }

    private Specification<Producto> construirSpec(
            String search,
            Long idCategoria,
            Long idMarca,
            String estado,
            boolean stockBajo) {

        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (estado != null && !estado.isBlank()) {
                predicados.add("1".equals(estado.trim())
                        ? cb.isTrue(root.<Boolean>get("estado"))
                        : cb.isFalse(root.<Boolean>get("estado")));
            }
            if (idCategoria != null) {
                predicados.add(cb.equal(root.get("categoriaProducto").get("idCategoriaProducto"), idCategoria));
            }
            if (idMarca != null) {
                predicados.add(cb.equal(root.get("marca").get("idMarca"), idMarca));
            }
            if (stockBajo) {
                predicados.add(cb.lessThanOrEqualTo(
                        root.<BigDecimal>get("stockActual"),
                        root.<BigDecimal>get("stockMinimo")));
            }
            if (search != null && !search.isBlank()) {
                String patron = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicados.add(cb.or(
                        cb.like(cb.lower(root.<String>get("nProducto")), patron),
                        cb.like(cb.lower(root.<String>get("codigoBarras")), patron),
                        cb.like(cb.lower(root.<String>get("detalle")), patron)));
            }

            return predicados.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    private Producto buscar(Long idProducto) {
        return productoRepository.findDetalle(idProducto)
                .orElseThrow(() -> noEncontrado("Producto", idProducto));
    }

    private CategoriaProducto buscarCategoriaActiva(Long idCategoria) {
        return categoriaProductoRepository.findByIdCategoriaProductoAndEstadoTrue(idCategoria)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La categoria seleccionada no esta disponible."));
    }

    private Marca buscarMarcaActiva(Long idMarca) {
        if (idMarca == null) {
            return null;
        }
        return marcaRepository.findByIdMarcaAndEstadoTrue(idMarca)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La marca seleccionada no esta disponible."));
    }

    private UnidadMedida buscarUnidadActiva(Long idUnidad) {
        return unidadMedidaRepository.findByIdUnidadMedidaAndEstadoTrue(idUnidad)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La unidad de medida seleccionada no esta disponible."));
    }

    private void aplicar(
            Producto producto,
            ProductoRequest request,
            CategoriaProducto categoria,
            Marca marca,
            String codigo) {

        producto.setCategoriaProducto(categoria);
        producto.setMarca(marca);
        producto.setCodigoBarras(codigo);
        producto.setNProducto(request.nProducto().trim());
        producto.setDetalle(limpiar(request.detalle()));
        producto.setPCompra(escalaPrecio(request.pCompra()));
        producto.setPVenta(escalaPrecio(request.pVenta()));
        producto.setPMayoreo(escalaPrecio(request.pMayoreo()));
        producto.setStockMinimo(escalaStock(request.stockMinimo()));
        producto.setStockMaximo(request.stockMaximo() == null ? null : escalaStock(request.stockMaximo()));
        producto.setAfectoIgv(booleano(request.afectoIgv(), true));
        producto.setEsPerecible(booleano(request.esPerecible(), false));
        producto.setImagen(limpiar(request.imagen()));
    }

    private void asegurarPresentacionBase(
            Producto producto,
            UnidadMedida unidad,
            BigDecimal factorConversion,
            Boolean estado) {

        PresentacionProducto presentacion = presentacionProductoRepository
                .findFirstByProducto_IdProductoAndUnidadMedida_IdUnidadMedida(
                        producto.getIdProducto(), unidad.getIdUnidadMedida())
                .orElseGet(PresentacionProducto::new);

        presentacion.setProducto(producto);
        presentacion.setUnidadMedida(unidad);
        presentacion.setFactorConversion(escalaFactor(factorConversion));
        presentacion.setEsUnidadBase(Boolean.TRUE);
        presentacion.setEstado(estado);
        presentacionProductoRepository.save(presentacion);
    }

    private void validarStockMaximo(BigDecimal stockMinimo, BigDecimal stockMaximo) {
        if (stockMaximo == null) {
            return;
        }
        BigDecimal minimo = stockMinimo == null ? BigDecimal.ZERO : stockMinimo;
        if (stockMaximo.compareTo(minimo) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El stock maximo no puede ser menor al stock minimo.");
        }
    }

    private ProductoDto aDto(Producto producto) {
        PresentacionProducto base = presentacionProductoRepository
                .findFirstByProducto_IdProductoAndEsUnidadBaseTrueAndEstadoTrue(producto.getIdProducto())
                .orElse(null);
        UnidadMedida unidad = base == null ? null : base.getUnidadMedida();
        CategoriaProducto categoria = producto.getCategoriaProducto();
        Marca marca = producto.getMarca();

        return new ProductoDto(
                producto.getIdProducto(),
                producto.getCodigoBarras(),
                producto.getNProducto(),
                producto.getDetalle(),
                categoria == null ? null : categoria.getIdCategoriaProducto(),
                categoria == null ? null : categoria.getNCategoriaProducto(),
                marca == null ? null : marca.getIdMarca(),
                marca == null ? null : marca.getNMarca(),
                producto.getPCompra(),
                producto.getPVenta(),
                producto.getPMayoreo(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getStockMaximo(),
                producto.getAfectoIgv(),
                producto.getEsPerecible(),
                producto.getImagen(),
                unidad == null ? null : unidad.getIdUnidadMedida(),
                unidad == null ? null : unidad.getNUnidadMedida(),
                unidad == null ? null : unidad.getAbreviatura(),
                producto.getEstado(),
                producto.getFecCreador());
    }

    private String normalizarCodigo(String codigo) {
        String limpio = limpiar(codigo);
        return limpio == null ? null : limpio.toUpperCase(Locale.ROOT);
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    private BigDecimal escalaPrecio(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(ESCALA_PRECIO, RoundingMode.HALF_UP);
    }

    private BigDecimal escalaStock(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(ESCALA_STOCK, RoundingMode.HALF_UP);
    }

    private BigDecimal escalaFactor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            return BigDecimal.ONE.setScale(ESCALA_STOCK, RoundingMode.HALF_UP);
        }
        return valor.setScale(ESCALA_STOCK, RoundingMode.HALF_UP);
    }

    private Boolean booleano(String valor, boolean defecto) {
        if (valor == null || valor.isBlank()) {
            return defecto;
        }
        return "1".equals(valor.trim());
    }

    private String resumen(ProductoDto producto) {
        return "codigo=" + texto(producto.codigoBarras())
                + "; nombre=" + texto(producto.nProducto())
                + "; categoria=" + texto(producto.nCategoriaProducto())
                + "; marca=" + texto(producto.nMarca())
                + "; pCompra=" + texto(producto.pCompra())
                + "; pVenta=" + texto(producto.pVenta())
                + "; stock=" + texto(producto.stockActual())
                + "; estado=" + (Boolean.TRUE.equals(producto.estado()) ? "1" : "0");
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private ResponseStatusException noEncontrado(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " no encontrado: " + id + ".");
    }

    private ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }
}

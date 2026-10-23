import { api, tieneCsrf, yo } from "../login/login.js";

const POR_PAGINA = 20;

const tbody = document.querySelector("#tablaProductos tbody");
const alertaLista = document.getElementById("productos-alerta");
const alertaModal = document.getElementById("producto-alerta");
const inputBuscar = document.getElementById("productos-buscar");
const filtroCategoria = document.getElementById("productos-filtro-categoria");
const filtroMarca = document.getElementById("productos-filtro-marca");
const filtroEstado = document.getElementById("productos-filtro-estado");
const filtroStockBajo = document.getElementById("productos-filtro-stock-bajo");
const infoPaginacion = document.getElementById("productos-paginacion-info");
const paginacion = document.getElementById("productos-paginacion");

const btnNuevo = document.getElementById("btn-nuevo-producto");
const modal = document.getElementById("modalProducto");
const form = document.getElementById("formProducto");
const tituloModal = document.getElementById("modalProductoLabel");
const btnGuardar = document.getElementById("btn-guardar-producto");

const inputNombre = document.getElementById("producto-nombre");
const inputCodigo = document.getElementById("producto-codigo");
const inputDetalle = document.getElementById("producto-detalle");
const inputImagen = document.getElementById("producto-imagen");
const selectCategoria = document.getElementById("producto-categoria");
const selectMarca = document.getElementById("producto-marca");
const selectUnidad = document.getElementById("producto-unidad");
const inputPCompra = document.getElementById("producto-p-compra");
const inputPVenta = document.getElementById("producto-p-venta");
const inputPMayoreo = document.getElementById("producto-p-mayoreo");
const avisoPrecio = document.getElementById("producto-precio-aviso");
const inputStockActual = document.getElementById("producto-stock-actual");
const inputStockMinimo = document.getElementById("producto-stock-minimo");
const inputStockMaximo = document.getElementById("producto-stock-maximo");
const inputAfectoIgv = document.getElementById("producto-afecto-igv");
const inputPerecible = document.getElementById("producto-perecible");

let productos = [];
let pagina = 0;
let totalRegistros = 0;
let editando = null;
let controladorListado = null;
let temporizadorBusqueda = null;
let temporizadorAlerta = null;
let promesaCatalogos = null;
let catalogosCargados = false;
let categorias = [];
let marcas = [];
let unidades = [];

/* ---------- Utilidades ---------- */

function leer(obj, claves) {
  if (!obj) {
    return undefined;
  }
  const clave = claves.find((c) => obj[c] !== undefined && obj[c] !== null);
  return clave === undefined ? undefined : obj[clave];
}

function aTexto(valor) {
  if (valor && typeof valor === "object") {
    return Object.values(valor).find((v) => typeof v === "string") || "";
  }
  return valor === undefined || valor === null ? "" : String(valor);
}

function numero(valor, defecto = 0) {
  const n = Number(valor);
  return Number.isFinite(n) ? n : defecto;
}

function normalizar(valor) {
  return aTexto(valor)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function esActivo(p) {
  const estado = leer(p, ["estado"]);
  return estado !== "0" && estado !== 0 && estado !== false;
}

function esUno(valor) {
  return valor === "1" || valor === 1 || valor === true;
}

function idProductoDe(p) {
  return leer(p, ["idProducto", "id_producto", "id"]);
}

function codigoDe(p) {
  return aTexto(leer(p, ["codigoBarras", "codigo_barras", "codigo"]));
}

function nombreProductoDe(p) {
  return aTexto(leer(p, ["nProducto", "n_producto", "nombre"]));
}

function detalleDe(p) {
  return aTexto(leer(p, ["detalle", "descripcion"]));
}

function imagenDe(p) {
  return aTexto(leer(p, ["imagen", "imagenUrl", "urlImagen"]));
}

function categoriaObjetoDe(p) {
  return leer(p, ["categoriaProducto", "categoria"]);
}

function idCategoriaDe(p) {
  const directo = leer(p, [
    "idCategoriaProducto",
    "id_categoria_producto",
    "idCategoria",
  ]);
  if (directo !== undefined) {
    return directo;
  }
  const cat = categoriaObjetoDe(p);
  return cat && typeof cat === "object" ? idDeCategoria(cat) : undefined;
}

function nombreCategoriaDe(p) {
  const directo = leer(p, [
    "nCategoriaProducto",
    "n_categoria_producto",
    "nombreCategoria",
  ]);
  if (directo !== undefined) {
    return aTexto(directo);
  }
  const cat = categoriaObjetoDe(p);
  return cat && typeof cat === "object" ? nombreDeCategoria(cat) : aTexto(cat);
}

function marcaObjetoDe(p) {
  return leer(p, ["marca", "marcaProducto"]);
}

function idMarcaDe(p) {
  const directo = leer(p, ["idMarca", "id_marca", "idMarcaProducto"]);
  if (directo !== undefined) {
    return directo;
  }
  const marca = marcaObjetoDe(p);
  return marca && typeof marca === "object" ? idDeMarca(marca) : undefined;
}

function nombreMarcaDe(p) {
  const directo = leer(p, ["nMarca", "n_marca", "nombreMarca"]);
  if (directo !== undefined) {
    return aTexto(directo);
  }
  const marca = marcaObjetoDe(p);
  return marca && typeof marca === "object" ? nombreDeMarca(marca) : aTexto(marca);
}

function presentacionDe(p) {
  return leer(p, [
    "presentacionBase",
    "presentacionProducto",
    "presentacion",
    "presentaciones",
  ]);
}

function idUnidadBaseDe(p) {
  const directo = leer(p, [
    "idUnidadMedidaBase",
    "id_unidad_medida_base",
    "idUnidadMedida",
  ]);
  if (directo !== undefined) {
    return directo;
  }
  const pres = presentacionDe(p);
  if (Array.isArray(pres)) {
    const base = pres.find((x) => esUno(leer(x, ["esUnidadBase", "es_unidad_base"])));
    return base ? idDeUnidad(base) : undefined;
  }
  return pres && typeof pres === "object" ? idDeUnidad(pres) : undefined;
}

function precioCompraDe(p) {
  return numero(leer(p, ["pCompra", "p_compra", "precioCompra"]));
}

function precioVentaDe(p) {
  return numero(leer(p, ["pVenta", "p_venta", "precioVenta"]));
}

function precioMayoreoDe(p) {
  return numero(leer(p, ["pMayoreo", "p_mayoreo", "precioMayoreo"]));
}

function stockActualDe(p) {
  return numero(leer(p, ["stockActual", "stock_actual", "stock"]));
}

function stockMinimoDe(p) {
  return numero(leer(p, ["stockMinimo", "stock_minimo"]));
}

function stockMaximoDe(p) {
  const valor = leer(p, ["stockMaximo", "stock_maximo"]);
  return valor === undefined || valor === null ? null : numero(valor);
}

function stockBajo(p) {
  return stockActualDe(p) <= stockMinimoDe(p);
}

function moneda(valor) {
  return "S/ " + numero(valor).toFixed(2);
}

function cantidad(valor) {
  const n = numero(valor);
  if (Number.isInteger(n)) {
    return String(n);
  }
  return n.toFixed(3).replace(/0+$/, "").replace(/\.$/, "");
}

/* ---------- Catálogos ---------- */

function idDeCategoria(c) {
  return leer(c, ["idCategoriaProducto", "id_categoria_producto", "id"]);
}

function nombreDeCategoria(c) {
  return aTexto(
    leer(c, ["nCategoriaProducto", "n_categoria_producto", "nombre"]),
  );
}

function idDeMarca(m) {
  return leer(m, ["idMarca", "id_marca", "id"]);
}

function nombreDeMarca(m) {
  return aTexto(leer(m, ["nMarca", "n_marca", "nombre"]));
}

function idDeUnidad(u) {
  return leer(u, ["idUnidadMedida", "id_unidad_medida", "id"]);
}

function nombreDeUnidad(u) {
  const nombre = leer(u, ["nUnidadMedida", "n_unidad_medida", "nombre"]);
  const abreviatura = leer(u, ["abreviatura", "abrev"]);
  if (nombre === undefined) {
    return aTexto(abreviatura);
  }
  return abreviatura ? `${aTexto(nombre)} (${aTexto(abreviatura)})` : aTexto(nombre);
}

function normalizarLista(res) {
  if (Array.isArray(res)) {
    return res;
  }
  if (!res || typeof res !== "object") {
    return [];
  }
  const posibles = [
    res.content,
    res.items,
    res.data,
    res.productos,
    res.registros,
    res.resultado,
  ];
  return posibles.find(Array.isArray) || [];
}

async function pedirPrimero(rutas) {
  let ultimo = null;
  for (const ruta of rutas) {
    try {
      return await api("GET", ruta);
    } catch (err) {
      if (err.status === 401) {
        throw err;
      }
      ultimo = err;
    }
  }
  throw ultimo || new Error("Catálogo no disponible.");
}

function llenarSelect(select, items, idFn, textoFn) {
  const valor = select.value;
  const primera = select.options[0];
  select.replaceChildren(primera);
  items.forEach((item) => {
    const opcion = document.createElement("option");
    opcion.value = idFn(item);
    opcion.textContent = textoFn(item);
    select.appendChild(opcion);
  });
  if (
    valor !== "" &&
    Array.from(select.options).some((o) => String(o.value) === String(valor))
  ) {
    select.value = valor;
  }
}

function cargarCatalogos() {
  if (catalogosCargados) {
    return Promise.resolve(true);
  }
  if (promesaCatalogos) {
    return promesaCatalogos;
  }

  promesaCatalogos = (async () => {
    try {
      const [cats, mars, unis] = await Promise.all([
        pedirPrimero([
          "/api/categorias-producto",
          "/api/categorias",
          "/api/productos/categorias",
        ]),
        pedirPrimero(["/api/marcas", "/api/productos/marcas"]),
        pedirPrimero([
          "/api/unidades-medida",
          "/api/unidades",
          "/api/productos/unidades",
        ]),
      ]);

      categorias = normalizarLista(cats);
      marcas = normalizarLista(mars);
      unidades = normalizarLista(unis);

      llenarSelect(filtroCategoria, categorias, idDeCategoria, nombreDeCategoria);
      llenarSelect(filtroMarca, marcas, idDeMarca, nombreDeMarca);
      llenarSelect(selectCategoria, categorias, idDeCategoria, nombreDeCategoria);
      llenarSelect(selectMarca, marcas, idDeMarca, nombreDeMarca);
      llenarSelect(selectUnidad, unidades, idDeUnidad, nombreDeUnidad);

      catalogosCargados = true;
      return true;
    } catch (err) {
      if (irALogin(err)) {
        return false;
      }
      mostrarAlertaLista(
        "danger",
        err.mensaje || "No se pudieron cargar los catálogos.",
      );
      return false;
    } finally {
      promesaCatalogos = null;
    }
  })();

  return promesaCatalogos;
}

/* ---------- Alertas ---------- */

function irALogin(err) {
  if (err.status === 401) {
    window.location.href = "../login/login.html";
    return true;
  }
  return false;
}

function mostrarAlertaLista(tipo, mensaje) {
  clearTimeout(temporizadorAlerta);
  alertaLista.className = "alert alert-" + tipo;
  alertaLista.textContent = mensaje;
  if (tipo === "success") {
    temporizadorAlerta = setTimeout(() => {
      alertaLista.classList.add("d-none");
    }, 4000);
  }
}

function mostrarAlertaModal(tipo, mensaje) {
  alertaModal.className = "alert alert-" + tipo;
  alertaModal.textContent = mensaje;
}

function limpiarAlertaModal() {
  alertaModal.className = "alert d-none";
  alertaModal.textContent = "";
}

/* ---------- Listado ---------- */

function celda(texto, clase) {
  const td = document.createElement("td");
  td.textContent = texto;
  if (clase) {
    td.className = clase;
  }
  return td;
}

function badge(texto, clase) {
  const span = document.createElement("span");
  span.className = "badge " + clase;
  span.textContent = texto;
  return span;
}

function filaMensaje(texto) {
  const tr = document.createElement("tr");
  const td = celda(texto, "text-center text-muted");
  td.colSpan = 9;
  tr.appendChild(td);
  return tr;
}

function filaProducto(p, indice) {
  const tr = document.createElement("tr");
  const activo = esActivo(p);
  if (!activo) {
    tr.classList.add("text-muted", "opacity-75");
  }

  tr.appendChild(celda(indice + 1));
  tr.appendChild(celda(codigoDe(p) || "—", "font-monospace small"));

  const tdNombre = document.createElement("td");
  const nombre = document.createElement("div");
  nombre.textContent = nombreProductoDe(p);
  tdNombre.appendChild(nombre);
  if (detalleDe(p)) {
    const detalle = document.createElement("small");
    detalle.className = "text-muted d-block";
    detalle.textContent = detalleDe(p);
    tdNombre.appendChild(detalle);
  }
  tr.appendChild(tdNombre);

  tr.appendChild(celda(nombreCategoriaDe(p) || "—"));
  tr.appendChild(celda(nombreMarcaDe(p) || "—"));
  tr.appendChild(celda(moneda(precioVentaDe(p)), "text-end"));

  const tdStock = document.createElement("td");
  tdStock.className = "text-end";
  tdStock.appendChild(
    badge(
      cantidad(stockActualDe(p)),
      stockBajo(p) ? "text-bg-danger" : "text-bg-light border",
    ),
  );
  const minimo = stockMinimoDe(p);
  if (minimo > 0) {
    const referencia = document.createElement("small");
    referencia.className = "text-muted d-block";
    referencia.textContent = "mín. " + cantidad(minimo);
    tdStock.appendChild(referencia);
  }
  tr.appendChild(tdStock);

  const tdEstado = document.createElement("td");
  tdEstado.appendChild(
    badge(
      activo ? "Activo" : "Inactivo",
      activo ? "text-bg-success" : "text-bg-secondary",
    ),
  );
  tr.appendChild(tdEstado);

  const tdAcciones = document.createElement("td");
  tdAcciones.innerHTML =
    '<div class="d-flex gap-1">' +
    '<button class="btn btn-sm btn-outline-primary" title="Editar" data-accion="editar"><i class="bi bi-pencil"></i></button>' +
    (activo
      ? '<button class="btn btn-sm btn-outline-primary" title="Desactivar" data-accion="desactivar"><i class="bi bi-toggle-on"></i></button>'
      : '<button class="btn btn-sm btn-outline-success" title="Activar" data-accion="activar"><i class="bi bi-toggle-off"></i></button>') +
    "</div>";
  tdAcciones.querySelectorAll("button").forEach((b) => {
    b.dataset.id = idProductoDe(p);
  });
  tr.appendChild(tdAcciones);

  return tr;
}

function renderTabla() {
  if (!productos.length) {
    tbody.replaceChildren(filaMensaje("No se encontraron productos."));
    renderPaginacion();
    return;
  }
  const offset = pagina * POR_PAGINA;
  tbody.replaceChildren(...productos.map((p, i) => filaProducto(p, offset + i)));
  renderPaginacion();
}

function crearItemPagina(texto, destino, deshabilitado, activo) {
  const li = document.createElement("li");
  li.className =
    "page-item" + (deshabilitado ? " disabled" : "") + (activo ? " active" : "");
  const boton = document.createElement("button");
  boton.type = "button";
  boton.className = "page-link";
  boton.textContent = texto;
  boton.dataset.pagina = destino;
  if (activo) {
    boton.setAttribute("aria-current", "page");
  }
  li.appendChild(boton);
  return li;
}

function renderPaginacion() {
  const totalPaginas = Math.max(1, Math.ceil(totalRegistros / POR_PAGINA));
  const inicio = totalRegistros ? pagina * POR_PAGINA + 1 : 0;
  const fin = Math.min(totalRegistros, (pagina + 1) * POR_PAGINA);
  infoPaginacion.textContent = totalRegistros
    ? `Mostrando ${inicio}–${fin} de ${totalRegistros}`
    : "Sin resultados";

  const items = [];
  items.push(crearItemPagina("«", pagina - 1, pagina === 0, false));

  const desde = Math.max(0, Math.min(pagina - 2, totalPaginas - 5));
  const hasta = Math.min(totalPaginas, desde + 5);
  for (let i = desde; i < hasta; i += 1) {
    items.push(crearItemPagina(String(i + 1), i, false, i === pagina));
  }

  items.push(
    crearItemPagina("»", pagina + 1, pagina >= totalPaginas - 1, false),
  );
  paginacion.replaceChildren(...items);
}

function normalizarPagina(res) {
  if (Array.isArray(res)) {
    return { items: res, total: res.length, servidor: false };
  }
  const items = normalizarLista(res);
  const total = leer(res, [
    "totalElements",
    "total",
    "totalRegistros",
    "count",
    "cantidad",
  ]);
  return {
    items,
    total: total === undefined ? items.length : numero(total),
    servidor: total !== undefined,
  };
}

function aplicarFiltros(lista) {
  const termino = normalizar(inputBuscar.value);
  const categoria = filtroCategoria.value;
  const marca = filtroMarca.value;
  const estado = filtroEstado.value;
  const soloStockBajo = filtroStockBajo.checked;

  return lista.filter((p) => {
    if (termino) {
      const texto = normalizar(
        `${codigoDe(p)} ${nombreProductoDe(p)} ${detalleDe(p)}`,
      );
      if (!texto.includes(termino)) {
        return false;
      }
    }
    if (categoria && String(idCategoriaDe(p)) !== String(categoria)) {
      return false;
    }
    if (marca && String(idMarcaDe(p)) !== String(marca)) {
      return false;
    }
    if (estado) {
      const activo = esActivo(p);
      if ((estado === "1") !== activo) {
        return false;
      }
    }
    if (soloStockBajo && !stockBajo(p)) {
      return false;
    }
    return true;
  });
}

function construirParametros() {
  const params = new URLSearchParams();
  params.set("page", pagina);
  params.set("size", POR_PAGINA);

  const termino = inputBuscar.value.trim();
  if (termino) {
    params.set("search", termino);
  }
  if (filtroCategoria.value) {
    params.set("idCategoria", filtroCategoria.value);
  }
  if (filtroMarca.value) {
    params.set("idMarca", filtroMarca.value);
  }
  if (filtroEstado.value) {
    params.set("estado", filtroEstado.value);
  }
  if (filtroStockBajo.checked) {
    params.set("stockBajo", "true");
  }
  return params;
}

async function listarProductos() {
  if (controladorListado) {
    controladorListado.abort();
  }
  controladorListado = new AbortController();
  const controlador = controladorListado;

  tbody.replaceChildren(filaMensaje("Cargando productos..."));

  try {
    const res = await api(
      "GET",
      "/api/productos?" + construirParametros().toString(),
      undefined,
      { signal: controlador.signal },
    );
    if (controlador.signal.aborted) {
      return;
    }

    const normalizado = normalizarPagina(res);
    if (normalizado.servidor) {
      productos = normalizado.items;
      totalRegistros = normalizado.total;
    } else {
      const filtrados = aplicarFiltros(normalizado.items);
      totalRegistros = filtrados.length;
      const inicio = pagina * POR_PAGINA;
      productos = filtrados.slice(inicio, inicio + POR_PAGINA);
    }

    const totalPaginas = Math.max(1, Math.ceil(totalRegistros / POR_PAGINA));
    if (pagina >= totalPaginas) {
      pagina = totalPaginas - 1;
    }

    renderTabla();
  } catch (err) {
    if (controlador.signal.aborted) {
      return;
    }
    if (irALogin(err)) {
      return;
    }
    productos = [];
    totalRegistros = 0;
    renderTabla();
    mostrarAlertaLista(
      "danger",
      err.mensaje || "No se pudieron cargar los productos.",
    );
  }
}

/* ---------- Formulario ---------- */

function agregarOpcionFaltante(select, valor, texto) {
  if (valor === undefined || valor === null) {
    return;
  }
  if (Array.from(select.options).some((o) => String(o.value) === String(valor))) {
    return;
  }
  const opcion = document.createElement("option");
  opcion.value = valor;
  opcion.textContent = texto || "Opción " + valor;
  select.appendChild(opcion);
}

function prepararModo() {
  const edicion = editando !== null;
  tituloModal.textContent = edicion ? "Editar Producto" : "Nuevo Producto";
  inputStockActual.disabled = edicion;
}

function cargarFormulario(p) {
  inputNombre.value = nombreProductoDe(p);
  inputCodigo.value = codigoDe(p);
  inputDetalle.value = detalleDe(p);
  inputImagen.value = imagenDe(p);

  const catId = idCategoriaDe(p);
  agregarOpcionFaltante(selectCategoria, catId, nombreCategoriaDe(p));
  selectCategoria.value = catId === undefined || catId === null ? "" : catId;

  const marId = idMarcaDe(p);
  agregarOpcionFaltante(selectMarca, marId, nombreMarcaDe(p));
  selectMarca.value = marId === undefined || marId === null ? "" : marId;

  const uniId = idUnidadBaseDe(p);
  agregarOpcionFaltante(selectUnidad, uniId, "Unidad " + aTexto(uniId));
  selectUnidad.value = uniId === undefined || uniId === null ? "" : uniId;

  inputPCompra.value = precioCompraDe(p);
  inputPVenta.value = precioVentaDe(p);
  inputPMayoreo.value = precioMayoreoDe(p);
  inputStockActual.value = stockActualDe(p);
  inputStockMinimo.value = stockMinimoDe(p);
  const maximo = stockMaximoDe(p);
  inputStockMaximo.value = maximo === null ? "" : maximo;

  const afecto = leer(p, ["afectoIgv", "afecto_igv"]);
  inputAfectoIgv.checked = afecto === undefined ? true : esUno(afecto);
  inputPerecible.checked = esUno(leer(p, ["esPerecible", "es_perecible"]));

  actualizarAvisoPrecio();
}

function actualizarAvisoPrecio() {
  const compra = numero(inputPCompra.value);
  const venta = numero(inputPVenta.value);
  if (inputPVenta.value !== "" && venta < compra) {
    avisoPrecio.className = "form-text text-warning";
    avisoPrecio.textContent =
      "Aviso: el precio de venta es menor al precio de compra.";
  } else {
    avisoPrecio.className = "form-text";
    avisoPrecio.textContent = "";
  }
}

function armarPayload() {
  const categoria = selectCategoria.value;
  const marca = selectMarca.value;
  const unidad = selectUnidad.value;

  const payload = {
    nProducto: inputNombre.value.trim(),
    codigoBarras: inputCodigo.value.trim() || null,
    detalle: inputDetalle.value.trim() || null,
    idCategoriaProducto: categoria === "" ? null : Number(categoria),
    idMarca: marca === "" ? null : Number(marca),
    pCompra: numero(inputPCompra.value),
    pVenta: numero(inputPVenta.value),
    pMayoreo: numero(inputPMayoreo.value),
    stockMinimo: numero(inputStockMinimo.value),
    stockMaximo:
      inputStockMaximo.value === "" ? null : numero(inputStockMaximo.value),
    afectoIgv: inputAfectoIgv.checked ? "1" : "0",
    esPerecible: inputPerecible.checked ? "1" : "0",
    imagen: inputImagen.value.trim() || null,
    idUnidadMedidaBase: unidad === "" ? null : Number(unidad),
    factorConversionBase: 1,
    esUnidadBase: "1",
  };

  if (editando === null) {
    payload.stockActual = numero(inputStockActual.value);
  }
  return payload;
}

function validar(payload) {
  const errores = [];
  if (!payload.nProducto) {
    errores.push("El nombre del producto es obligatorio.");
  } else if (payload.nProducto.length > 50) {
    errores.push("El nombre no debe superar los 50 caracteres.");
  }
  if (!payload.idCategoriaProducto) {
    errores.push("La categoría es obligatoria.");
  }
  if (!payload.idUnidadMedidaBase) {
    errores.push("La unidad base es obligatoria.");
  }
  if (inputPVenta.value === "" || payload.pVenta < 0) {
    errores.push("El precio de venta debe ser un número mayor o igual a 0.");
  }
  if (payload.pCompra < 0) {
    errores.push("El precio de compra no puede ser negativo.");
  }
  if (payload.pMayoreo < 0) {
    errores.push("El precio de mayoreo no puede ser negativo.");
  }
  if (payload.stockMinimo < 0) {
    errores.push("El stock mínimo no puede ser negativo.");
  }
  if (payload.stockMaximo !== null && payload.stockMaximo < payload.stockMinimo) {
    errores.push("El stock máximo no puede ser menor al stock mínimo.");
  }
  if (editando === null && payload.stockActual < 0) {
    errores.push("El stock inicial no puede ser negativo.");
  }
  return errores;
}

async function obtenerDetalle(p) {
  const id = idProductoDe(p);
  if (id === undefined || id === null) {
    return p;
  }
  try {
    return await api("GET", "/api/productos/" + id);
  } catch (err) {
    if (irALogin(err)) {
      throw err;
    }
    return p;
  }
}

async function guardarProducto() {
  limpiarAlertaModal();
  const payload = armarPayload();
  const errores = validar(payload);
  if (errores.length) {
    mostrarAlertaModal("danger", errores.join(" "));
    return;
  }

  const eraEdicion = editando !== null;
  btnGuardar.disabled = true;
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    if (eraEdicion) {
      await api("PUT", "/api/productos/" + idProductoDe(editando), payload);
    } else {
      await api("POST", "/api/productos", payload);
    }
    bootstrap.Modal.getOrCreateInstance(modal).hide();
    await listarProductos();
    mostrarAlertaLista(
      "success",
      eraEdicion ? "Producto actualizado." : "Producto registrado.",
    );
  } catch (err) {
    if (!irALogin(err)) {
      mostrarAlertaModal(
        "danger",
        err.mensaje || "No se pudo guardar el producto.",
      );
    }
  } finally {
    btnGuardar.disabled = false;
  }
}

async function cambiarEstado(p, activar) {
  const nombre = nombreProductoDe(p);
  const pregunta = activar
    ? `¿Activar el producto "${nombre}"?`
    : `¿Desactivar el producto "${nombre}"?`;
  if (!window.confirm(pregunta)) {
    return;
  }

  const id = idProductoDe(p);
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    if (activar) {
      await api("PATCH", `/api/productos/${id}/estado`, { estado: "1" });
    } else {
      await api("DELETE", `/api/productos/${id}`);
    }
    await listarProductos();
    mostrarAlertaLista(
      "success",
      activar ? "Producto activado." : "Producto desactivado.",
    );
  } catch (err) {
    if (!irALogin(err)) {
      mostrarAlertaLista(
        "danger",
        err.mensaje || "No se pudo actualizar el estado del producto.",
      );
    }
  }
}

/* ---------- Eventos ---------- */

inputBuscar.addEventListener("input", () => {
  clearTimeout(temporizadorBusqueda);
  temporizadorBusqueda = setTimeout(() => {
    pagina = 0;
    listarProductos();
  }, 350);
});

[filtroCategoria, filtroMarca, filtroEstado].forEach((select) => {
  select.addEventListener("change", () => {
    pagina = 0;
    listarProductos();
  });
});

filtroStockBajo.addEventListener("change", () => {
  pagina = 0;
  listarProductos();
});

paginacion.addEventListener("click", (e) => {
  const boton = e.target.closest("button[data-pagina]");
  if (!boton || boton.closest(".disabled")) {
    return;
  }
  const destino = Number(boton.dataset.pagina);
  if (!Number.isFinite(destino) || destino === pagina) {
    return;
  }
  const totalPaginas = Math.max(1, Math.ceil(totalRegistros / POR_PAGINA));
  pagina = Math.max(0, Math.min(totalPaginas - 1, destino));
  listarProductos();
});

tbody.addEventListener("click", (e) => {
  const boton = e.target.closest("button[data-accion]");
  if (!boton) {
    return;
  }
  const p = productos.find(
    (x) => String(idProductoDe(x)) === String(boton.dataset.id),
  );
  if (!p) {
    return;
  }
  if (boton.dataset.accion === "editar") {
    editando = p;
    bootstrap.Modal.getOrCreateInstance(modal).show();
  } else if (boton.dataset.accion === "activar") {
    cambiarEstado(p, true);
  } else {
    cambiarEstado(p, false);
  }
});

btnNuevo.addEventListener("click", () => {
  editando = null;
});

modal.addEventListener("show.bs.modal", async () => {
  limpiarAlertaModal();
  if (editando === null) {
    form.reset();
  }
  prepararModo();
  actualizarAvisoPrecio();

  const ok = await cargarCatalogos();
  if (!ok || editando === null) {
    return;
  }
  try {
    const detalle = await obtenerDetalle(editando);
    cargarFormulario(detalle);
  } catch (err) {
    if (!irALogin(err)) {
      mostrarAlertaModal(
        "danger",
        err.mensaje || "No se pudo cargar el producto.",
      );
    }
  }
});

modal.addEventListener("hidden.bs.modal", () => {
  editando = null;
  form.reset();
  limpiarAlertaModal();
});

btnGuardar.addEventListener("click", guardarProducto);
inputPCompra.addEventListener("input", actualizarAvisoPrecio);
inputPVenta.addEventListener("input", actualizarAvisoPrecio);

cargarCatalogos();
listarProductos();

if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-productos", listarProductos);
}

import { api, tieneCsrf, yo } from "../login/login.js";
import { FACTILIZA_API_BASE, FACTILIZA_API_TOKEN } from "../assets/config.js";

const modal = document.getElementById("modalCliente");
const form = document.getElementById("formCliente");
const titulo = document.getElementById("modalClienteLabel");
const alerta = document.getElementById("cliente-alerta");
const btnGuardar = modal.querySelector(".modal-footer .btn-primary");
const btnNuevo = document.getElementById("btn-nuevo-cliente");
const tbody = document.querySelector("#pane-clientes tbody");
const inputBuscar = document.getElementById("clientes-buscar");
const filtroTipo = document.getElementById("filtro-clientes-tipo");
const filtroEstadoCliente = document.getElementById("filtro-clientes-estado");
const infoPaginacionCliente = document.getElementById("clientes-paginacion-info");
const paginacionCliente = document.getElementById("clientes-paginacion");
const inputDocumentoCliente = document.getElementById("cliente-documento");
const selectTipoDocCliente = document.getElementById("cliente-tipo-doc");

const tiposIdentidad = new Map();
const clientes = new Map();
const listaClientes = [];

let catalogosPromise = null;
let editando = null;
let tipoClienteActual = "N";

let autocompletarClienteTimer = null;

function esDni(texto) {
  const t = (texto || "").toUpperCase();
  return t.includes("DNI");
}

function esRuc(texto) {
  const t = (texto || "").toUpperCase();
  return t.includes("RUC");
}

async function consultarFactiliza(tipo, documento) {
  if (!FACTILIZA_API_TOKEN || FACTILIZA_API_TOKEN === "PON_AQUI_TU_TOKEN_FACTILIZA") {
    return null;
  }
  const doc = (documento || "").trim();
  if (!doc) {
    return null;
  }
  try {
    const ruta = tipo === "ruc" ? "/ruc/info/" + encodeURIComponent(doc) : "/dni/info/" + encodeURIComponent(doc);
    const res = await fetch(FACTILIZA_API_BASE + ruta, {
      headers: {
        Authorization: "Bearer " + FACTILIZA_API_TOKEN,
        Accept: "application/json",
      },
    });
    if (!res.ok) {
      return null;
    }
    const datos = await res.json();
    return datos && datos.data ? datos.data : null;
  } catch (e) {
    return null;
  }
}

async function autocompletarDesdeDocumentoCliente() {
  if (!inputDocumentoCliente || !selectTipoDocCliente) {
    return;
  }
  const doc = inputDocumentoCliente.value.trim();
  if (!doc) {
    return;
  }
  const textoTipo = selectTipoDocCliente.options[selectTipoDocCliente.selectedIndex]?.textContent?.trim() || "";
  let info = null;
  if (esDni(textoTipo)) {
    info = await consultarFactiliza("dni", doc);
    if (info) {
      if (!document.getElementById("cliente-nombre").value.trim()) {
        setValor("cliente-nombre", info.nombres || "");
      }
      if (!document.getElementById("cliente-ap-paterno").value.trim()) {
        setValor("cliente-ap-paterno", info.apellido_paterno || "");
      }
      if (!document.getElementById("cliente-ap-materno").value.trim()) {
        setValor("cliente-ap-materno", info.apellido_materno || "");
      }
      if (!document.getElementById("cliente-direccion").value.trim() && info.direccion_completa) {
        setValor("cliente-direccion", info.direccion_completa);
      } else if (!document.getElementById("cliente-direccion").value.trim() && info.direccion) {
        setValor("cliente-direccion", info.direccion);
      }
    }
  } else if (esRuc(textoTipo)) {
    info = await consultarFactiliza("ruc", doc);
    if (info) {
      if (!document.getElementById("cliente-nombre").value.trim()) {
        setValor("cliente-nombre", info.nombre_o_razon_social || "");
      }
      if (!document.getElementById("cliente-direccion").value.trim() && info.direccion_completa) {
        setValor("cliente-direccion", info.direccion_completa);
      } else if (!document.getElementById("cliente-direccion").value.trim() && info.direccion) {
        setValor("cliente-direccion", info.direccion);
      }
    }
  }
}

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

function normalizarBusqueda(valor) {
  return aTexto(valor)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function mostrarError(mensaje) {
  alerta.textContent = mensaje;
  alerta.classList.remove("d-none");
}

function limpiarError() {
  alerta.textContent = "";
  alerta.classList.add("d-none");
}

function irALogin(err) {
  if (err.status === 401) {
    window.location.href = "../login/login.html";
    return true;
  }
  return false;
}

/* ---------- Catálogos ---------- */

function llenarTiposIdentidad(items) {
  const select = document.getElementById("cliente-tipo-doc");
  const placeholder = select.querySelector('option[value=""]');
  tiposIdentidad.clear();
  select.replaceChildren(placeholder);
  (items || []).forEach((item) => {
    const id = leer(item, ["idTipoIdentidad", "id"]);
    const texto = aTexto(leer(item, ["nTipoIdentidad", "nombre"]));
    tiposIdentidad.set(String(id), texto);
    const opcion = document.createElement("option");
    opcion.value = id;
    opcion.textContent = texto;
    select.appendChild(opcion);
  });
}

function cargarCatalogos() {
  if (catalogosPromise) {
    return catalogosPromise;
  }
  catalogosPromise = api("GET", "/api/tipos-identidad")
    .then((items) => {
      llenarTiposIdentidad(items);
      return true;
    })
    .catch((err) => {
      catalogosPromise = null;
      if (!irALogin(err)) {
        mostrarError(err.mensaje || "No se pudieron cargar los tipos de documento.");
      }
      throw err;
    });
  return catalogosPromise;
}

/* ---------- Listado ---------- */

function celda(texto) {
  const td = document.createElement("td");
  td.textContent = texto;
  return td;
}

function nombreCompleto(persona) {
  const nombre = leer(persona, ["nombre"]);
  const apellidos = [leer(persona, ["apPaterno"]), leer(persona, ["apMaterno"])]
    .filter(Boolean)
    .join(" ");
  return [nombre, apellidos].filter(Boolean).join(" ");
}

function documentoTexto(persona) {
  const tipo = tiposIdentidad.get(String(leer(persona, ["idTipoIdentidad"])));
  return [tipo, leer(persona, ["nDocumento"])].filter(Boolean).join(" ");
}

function estaActivo(registro) {
  return registro.estado !== false && registro.estado !== "0";
}

function filaCliente(cliente, indice) {
  const persona = cliente.persona || {};
  const activo = estaActivo(cliente);
  const id = cliente.idCliente;
  const tr = document.createElement("tr");

  tr.appendChild(celda(id ?? indice + 1));
  tr.appendChild(celda(documentoTexto(persona)));
  tr.appendChild(celda(nombreCompleto(persona)));
  tr.appendChild(celda(aTexto(leer(persona, ["celular"]))));
  tr.appendChild(celda(aTexto(leer(persona, ["direccion"]))));

  const tdEstado = document.createElement("td");
  const badge = document.createElement("span");
  badge.className =
    "badge " + (activo ? "text-bg-success" : "text-bg-secondary");
  badge.textContent = activo ? "Activo" : "Inactivo";
  tdEstado.appendChild(badge);
  tr.appendChild(tdEstado);

  const tdAcciones = document.createElement("td");
  tdAcciones.innerHTML =
    '<div class="d-flex gap-1">' +
    '<button class="btn btn-sm btn-outline-primary" title="Editar" data-accion="editar" data-id="' +
    id +
    '"><i class="bi bi-pencil"></i></button>' +
    (activo
      ? '<button class="btn btn-sm btn-outline-primary" title="Desactivar" data-accion="estado" data-id="' +
        id +
        '"><i class="bi bi-toggle-on"></i></button>'
      : '<button class="btn btn-sm btn-outline-success" title="Activar" data-accion="estado" data-id="' +
        id +
        '"><i class="bi bi-toggle-off"></i></button>') +
    "</div>";
  tr.appendChild(tdAcciones);

  return tr;
}

function filaMensaje(texto) {
  const tr = document.createElement("tr");
  const td = celda(texto);
  td.colSpan = 8;
  td.className = "text-center text-muted";
  tr.appendChild(td);
  return tr;
}

let paginaClientes = 0;
let totalClientesFiltrados = 0;
const POR_PAGINA_CLIENTES = 10;

function renderPaginacionClientes() {
  const totalPaginas = Math.max(1, Math.ceil(totalClientesFiltrados / POR_PAGINA_CLIENTES));
  if (infoPaginacionCliente) {
    const inicio = totalClientesFiltrados ? paginaClientes * POR_PAGINA_CLIENTES + 1 : 0;
    const fin = Math.min(totalClientesFiltrados, (paginaClientes + 1) * POR_PAGINA_CLIENTES);
    infoPaginacionCliente.textContent = totalClientesFiltrados
      ? `Mostrando ${inicio} - ${fin} de ${totalClientesFiltrados}`
      : "Sin resultados";
  }
  if (!paginacionCliente) {
    return;
  }
  paginacionCliente.replaceChildren();
  const liAnt = document.createElement("li");
  liAnt.className = "page-item" + (paginaClientes === 0 ? " disabled" : "");
  const btnAnt = document.createElement("button");
  btnAnt.className = "page-link";
  btnAnt.type = "button";
  btnAnt.textContent = "«";
  btnAnt.addEventListener("click", () => {
    if (paginaClientes > 0) {
      paginaClientes -= 1;
      renderClientes();
    }
  });
  liAnt.appendChild(btnAnt);
  paginacionCliente.appendChild(liAnt);
  const maxVisibles = 5;
  let desde = Math.max(0, paginaClientes - Math.floor(maxVisibles / 2));
  let hasta = Math.min(totalPaginas, desde + maxVisibles);
  if (hasta - desde < maxVisibles) {
    desde = Math.max(0, hasta - maxVisibles);
  }
  for (let i = desde; i < hasta; i += 1) {
    const li = document.createElement("li");
    li.className = "page-item" + (i === paginaClientes ? " active" : "");
    const btn = document.createElement("button");
    btn.className = "page-link";
    btn.type = "button";
    btn.textContent = String(i + 1);
    btn.addEventListener("click", () => {
      paginaClientes = i;
      renderClientes();
    });
    li.appendChild(btn);
    paginacionCliente.appendChild(li);
  }
  const liSig = document.createElement("li");
  liSig.className = "page-item" + (paginaClientes >= totalPaginas - 1 ? " disabled" : "");
  const btnSig = document.createElement("button");
  btnSig.className = "page-link";
  btnSig.type = "button";
  btnSig.textContent = "»";
  btnSig.addEventListener("click", () => {
    if (paginaClientes < totalPaginas - 1) {
      paginaClientes += 1;
      renderClientes();
    }
  });
  liSig.appendChild(btnSig);
  paginacionCliente.appendChild(liSig);
}

function textoBusquedaCliente(cliente) {
  const persona = cliente.persona || {};
  return normalizarBusqueda(
    [
      documentoTexto(persona),
      leer(persona, ["nDocumento"]),
      nombreCompleto(persona),
      leer(persona, ["celular"]),
      leer(persona, ["email"]),
      leer(persona, ["direccion"]),
      leer(cliente, ["codigoCliente"]),
    ]
      .filter(Boolean)
      .join(" ")
  );
}

function renderClientes() {
  const termino = normalizarBusqueda(inputBuscar?.value);
  const tipo = filtroTipo?.value || "";
  const estado = filtroEstadoCliente?.value || "";
  let visibles = listaClientes.filter((cliente) => {
    const coincideTexto = !termino || textoBusquedaCliente(cliente).includes(termino);
    const coincideTipo = !tipo || aTexto(leer(cliente, ["tipoCliente"])) === tipo;
    const coincideEstado = !estado || (estado === "1" ? estaActivo(cliente) : !estaActivo(cliente));
    return coincideTexto && coincideTipo && coincideEstado;
  });

  if (!listaClientes.length) {
    tbody.replaceChildren(filaMensaje("No hay clientes registrados."));
    totalClientesFiltrados = 0;
    renderPaginacionClientes();
    return;
  }
  if (!visibles.length) {
    tbody.replaceChildren(filaMensaje("No se encontraron clientes."));
    totalClientesFiltrados = 0;
    renderPaginacionClientes();
    return;
  }

  totalClientesFiltrados = visibles.length;
  const totalPaginas = Math.max(1, Math.ceil(totalClientesFiltrados / POR_PAGINA_CLIENTES));
  if (paginaClientes >= totalPaginas) {
    paginaClientes = totalPaginas - 1;
  }
  if (paginaClientes < 0) {
    paginaClientes = 0;
  }
  const inicio = paginaClientes * POR_PAGINA_CLIENTES;
  visibles = visibles.slice(inicio, inicio + POR_PAGINA_CLIENTES);
  tbody.replaceChildren(...visibles.map(filaCliente));
  renderPaginacionClientes();
}

async function listarClientes() {
  try {
    const lista = (await api("GET", "/api/clientes")) || [];
    listaClientes.length = 0;
    clientes.clear();
    lista.forEach((cliente) => {
      listaClientes.push(cliente);
      clientes.set(cliente.idCliente, cliente);
    });
    renderClientes();
  } catch (err) {
    if (!irALogin(err)) {
      tbody.replaceChildren(filaMensaje(err.mensaje || "No se pudo cargar."));
    }
  }
}

/* ---------- Formulario ---------- */

function valor(id) {
  return document.getElementById(id).value.trim();
}

function textoONulo(id) {
  return valor(id) || null;
}

function numeroONulo(id) {
  const v = valor(id);
  return v === "" ? null : Number(v);
}

function setValor(id, dato) {
  document.getElementById(id).value =
    dato === undefined || dato === null ? "" : dato;
}

function armarPayload() {
  return {
    persona: {
      idTipoIdentidad: numeroONulo("cliente-tipo-doc"),
      nDocumento: valor("cliente-documento"),
      nombre: valor("cliente-nombre"),
      apPaterno: textoONulo("cliente-ap-paterno"),
      apMaterno: textoONulo("cliente-ap-materno"),
      fNacimiento: textoONulo("cliente-nacimiento"),
      email: textoONulo("cliente-email"),
      celular: textoONulo("cliente-celular"),
      genero: textoONulo("cliente-genero"),
      direccion: textoONulo("cliente-direccion"),
    },
    codigoCliente: textoONulo("cliente-codigo"),
    tipoCliente: tipoClienteActual,
    limiteCredito: numeroONulo("cliente-limite"),
    puntos: numeroONulo("cliente-puntos"),
  };
}

function validar(payload) {
  const errores = [];
  if (!payload.persona.idTipoIdentidad) {
    errores.push("El tipo de documento es obligatorio.");
  }
  if (!payload.persona.nDocumento) {
    errores.push("El número de documento es obligatorio.");
  } else {
    const selectTipo = document.getElementById("cliente-tipo-doc");
    const textoTipo =
      selectTipo.options[selectTipo.selectedIndex]?.textContent?.trim() || "";
    if (textoTipo.toUpperCase().includes("DNI") && payload.persona.nDocumento.length !== 8) {
      errores.push("El DNI debe tener 8 caracteres.");
    } else if (textoTipo.toUpperCase().includes("RUC") && payload.persona.nDocumento.length !== 11) {
      errores.push("El RUC debe tener 11 caracteres.");
    } else if (
      textoTipo.toUpperCase().includes("CARNET") ||
      textoTipo.toUpperCase().includes("PASAPORTE")
    ) {
      if (payload.persona.nDocumento.length < 6 || payload.persona.nDocumento.length > 12) {
        errores.push("El documento debe tener entre 6 y 12 caracteres.");
      }
    }
  }
  if (!payload.persona.nombre) {
    errores.push("Los nombres son obligatorios.");
  }
  return errores;
}

function llenarFormulario(cliente) {
  const persona = cliente.persona || {};
  form.reset();
  setValor("cliente-tipo-doc", leer(persona, ["idTipoIdentidad"]));
  setValor("cliente-documento", leer(persona, ["nDocumento"]));
  setValor("cliente-nombre", leer(persona, ["nombre"]));
  setValor("cliente-ap-paterno", leer(persona, ["apPaterno"]));
  setValor("cliente-ap-materno", leer(persona, ["apMaterno"]));
  setValor("cliente-nacimiento", leer(persona, ["fNacimiento"]));
  setValor("cliente-genero", leer(persona, ["genero"]));
  setValor("cliente-celular", leer(persona, ["celular"]));
  setValor("cliente-email", leer(persona, ["email"]));
  setValor("cliente-direccion", leer(persona, ["direccion"]));
  setValor("cliente-codigo", leer(cliente, ["codigoCliente"]));
  setValor("cliente-limite", leer(cliente, ["limiteCredito"]));
  setValor("cliente-puntos", leer(cliente, ["puntos"]));
}

async function abrirNuevo() {
  editando = null;
  tipoClienteActual = "N";
  form.reset();
  document.getElementById("cliente-tipo-doc").value = "";
  titulo.textContent = "Nuevo Cliente";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
  try {
    await cargarCatalogos();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudieron cargar los tipos de documento.");
    }
  }
}

async function abrirEditar(id) {
  const cliente = clientes.get(id);
  if (!cliente) {
    return;
  }
  editando = id;
  tipoClienteActual = leer(cliente, ["tipoCliente"]) || "N";
  titulo.textContent = "Editar Cliente";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
  try {
    await cargarCatalogos();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudieron cargar los tipos de documento.");
    }
  }
  llenarFormulario(cliente);
}

async function guardarCliente() {
  limpiarError();
  const payload = armarPayload();
  const errores = validar(payload);
  if (errores.length) {
    mostrarError(errores.join(" "));
    return;
  }

  btnGuardar.disabled = true;
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    if (editando === null) {
      await api("POST", "/api/clientes", payload);
    } else {
      await api("PUT", "/api/clientes/" + editando, payload);
    }
    form.reset();
    editando = null;
    bootstrap.Modal.getOrCreateInstance(modal).hide();
    await listarClientes();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudo guardar el cliente.");
    }
  } finally {
    btnGuardar.disabled = false;
  }
}

async function cambiarEstado(id) {
  const cliente = clientes.get(id);
  if (!cliente) {
    return;
  }
  const activo = estaActivo(cliente);
  const accion = activo ? "Desactivar" : "Activar";
  if (!window.confirm(accion + " este cliente?")) {
    return;
  }
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    await api("PATCH", "/api/clientes/" + id + "/estado", {
      estado: activo ? "0" : "1",
    });
    await listarClientes();
  } catch (err) {
    if (!irALogin(err)) {
      window.alert(err.mensaje || "No se pudo cambiar el estado.");
    }
  }
}

/* ---------- Eventos ---------- */

btnNuevo.addEventListener("click", abrirNuevo);
btnGuardar.addEventListener("click", guardarCliente);
inputBuscar.addEventListener("input", () => {
  paginaClientes = 0;
  renderClientes();
});
filtroTipo.addEventListener("change", () => {
  paginaClientes = 0;
  renderClientes();
});
filtroEstadoCliente?.addEventListener("change", () => {
  paginaClientes = 0;
  renderClientes();
});

if (inputDocumentoCliente) {
  inputDocumentoCliente.addEventListener("input", () => {
    clearTimeout(autocompletarClienteTimer);
    autocompletarClienteTimer = setTimeout(() => {
      autocompletarDesdeDocumentoCliente();
    }, 500);
  });
}

if (selectTipoDocCliente) {
  selectTipoDocCliente.addEventListener("change", () => {
    clearTimeout(autocompletarClienteTimer);
    autocompletarClienteTimer = setTimeout(() => {
      autocompletarDesdeDocumentoCliente();
    }, 300);
  });
}

tbody.addEventListener("click", (evento) => {
  const boton = evento.target.closest("button[data-accion]");
  if (!boton) {
    return;
  }
  const id = Number(boton.dataset.id);
  if (boton.dataset.accion === "editar") {
    abrirEditar(id);
  } else {
    cambiarEstado(id);
  }
});

async function iniciar() {
  try {
    await cargarCatalogos();
  } catch (err) {
    // El listado se carga igual; el error se muestra al abrir el modal.
  }
  await listarClientes();
}

iniciar();

if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-clientes", listarClientes);
}

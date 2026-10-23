import { api, tieneCsrf, yo } from "../login/login.js";
import { FACTILIZA_API_BASE, FACTILIZA_API_TOKEN } from "../assets/config.js";

const modal = document.getElementById("modalProveedor");
const form = document.getElementById("formProveedor");
const titulo = document.getElementById("modalProveedorLabel");
const alerta = document.getElementById("proveedor-alerta");
const btnGuardar = modal.querySelector(".modal-footer .btn-primary");
const btnNuevo = document.getElementById("btn-nuevo-proveedor");
const tbody = document.querySelector("#pane-proveedores tbody");
const inputBuscar = document.getElementById("proveedores-buscar");
const filtroCredito = document.getElementById("filtro-proveedores-credito");
const inputDocumentoProveedor = document.getElementById("proveedor-documento");
const selectTipoDocProveedor = document.getElementById("proveedor-tipo-doc");

const tiposIdentidad = new Map();
const proveedores = new Map();
const listaProveedores = [];

let catalogosPromise = null;
let editando = null;

let autocompletarProveedorTimer = null;

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

async function autocompletarDesdeDocumentoProveedor() {
  if (!inputDocumentoProveedor || !selectTipoDocProveedor) {
    return;
  }
  const doc = inputDocumentoProveedor.value.trim();
  if (!doc) {
    return;
  }
  const textoTipo = selectTipoDocProveedor.options[selectTipoDocProveedor.selectedIndex]?.textContent?.trim() || "";
  let info = null;
  if (esDni(textoTipo)) {
    info = await consultarFactiliza("dni", doc);
    if (info) {
      if (!document.getElementById("proveedor-nombre").value.trim()) {
        setValor("proveedor-nombre", info.nombres || "");
      }
      if (!document.getElementById("proveedor-ap-paterno").value.trim()) {
        setValor("proveedor-ap-paterno", info.apellido_paterno || "");
      }
      if (!document.getElementById("proveedor-ap-materno").value.trim()) {
        setValor("proveedor-ap-materno", info.apellido_materno || "");
      }
      if (!document.getElementById("proveedor-direccion").value.trim() && info.direccion_completa) {
        setValor("proveedor-direccion", info.direccion_completa);
      } else if (!document.getElementById("proveedor-direccion").value.trim() && info.direccion) {
        setValor("proveedor-direccion", info.direccion);
      }
    }
  } else if (esRuc(textoTipo)) {
    info = await consultarFactiliza("ruc", doc);
    if (info) {
      if (!document.getElementById("proveedor-nombre").value.trim()) {
        setValor("proveedor-nombre", info.nombre_o_razon_social || "");
      }
      if (!document.getElementById("proveedor-direccion").value.trim() && info.direccion_completa) {
        setValor("proveedor-direccion", info.direccion_completa);
      } else if (!document.getElementById("proveedor-direccion").value.trim() && info.direccion) {
        setValor("proveedor-direccion", info.direccion);
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
  const select = document.getElementById("proveedor-tipo-doc");
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

function filaProveedor(proveedor, indice) {
  const persona = proveedor.persona || {};
  const activo = estaActivo(proveedor);
  const id = proveedor.idProveedor;
  const tr = document.createElement("tr");

  tr.appendChild(celda(id ?? indice + 1));
  tr.appendChild(celda(documentoTexto(persona)));
  tr.appendChild(celda(nombreCompleto(persona)));
  tr.appendChild(celda(aTexto(leer(proveedor, ["contacto"]))));
  tr.appendChild(celda(aTexto(leer(proveedor, ["telefonoContacto"]))));
  tr.appendChild(celda(aTexto(leer(proveedor, ["emailContacto"]))));

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

function textoBusquedaProveedor(proveedor) {
  const persona = proveedor.persona || {};
  return normalizarBusqueda(
    [
      documentoTexto(persona),
      leer(persona, ["nDocumento"]),
      nombreCompleto(persona),
      leer(persona, ["celular"]),
      leer(persona, ["email"]),
      leer(persona, ["direccion"]),
      leer(proveedor, ["codigoProveedor"]),
      leer(proveedor, ["contacto"]),
      leer(proveedor, ["telefonoContacto"]),
      leer(proveedor, ["emailContacto"]),
    ]
      .filter(Boolean)
      .join(" ")
  );
}

function renderProveedores() {
  const termino = normalizarBusqueda(inputBuscar?.value);
  const soloCredito = Boolean(filtroCredito?.checked);
  const visibles = listaProveedores.filter((proveedor) => {
    const coincideTexto =
      !termino || textoBusquedaProveedor(proveedor).includes(termino);
    const dias = Number(leer(proveedor, ["diasCredito"]) ?? 0);
    const coincideCredito = !soloCredito || dias > 0;
    return coincideTexto && coincideCredito;
  });

  if (!listaProveedores.length) {
    tbody.replaceChildren(filaMensaje("No hay proveedores registrados."));
    return;
  }
  if (!visibles.length) {
    tbody.replaceChildren(filaMensaje("No se encontraron proveedores."));
    return;
  }
  tbody.replaceChildren(...visibles.map(filaProveedor));
}

async function listarProveedores() {
  try {
    const lista = (await api("GET", "/api/proveedores")) || [];
    listaProveedores.length = 0;
    proveedores.clear();
    lista.forEach((proveedor) => {
      listaProveedores.push(proveedor);
      proveedores.set(proveedor.idProveedor, proveedor);
    });
    renderProveedores();
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
      idTipoIdentidad: numeroONulo("proveedor-tipo-doc"),
      nDocumento: valor("proveedor-documento"),
      nombre: valor("proveedor-nombre"),
      apPaterno: textoONulo("proveedor-ap-paterno"),
      apMaterno: textoONulo("proveedor-ap-materno"),
      fNacimiento: textoONulo("proveedor-nacimiento"),
      email: textoONulo("proveedor-email"),
      celular: textoONulo("proveedor-celular"),
      genero: textoONulo("proveedor-genero"),
      direccion: textoONulo("proveedor-direccion"),
    },
    codigoProveedor: textoONulo("proveedor-codigo"),
    contacto: textoONulo("proveedor-contacto"),
    telefonoContacto: textoONulo("proveedor-telefono-contacto"),
    emailContacto: textoONulo("proveedor-email-contacto"),
    diasCredito: numeroONulo("proveedor-dias-credito"),
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
    const selectTipo = document.getElementById("proveedor-tipo-doc");
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
    errores.push("Los nombres o la razón social son obligatorios.");
  }
  return errores;
}

function llenarFormulario(proveedor) {
  const persona = proveedor.persona || {};
  form.reset();
  setValor("proveedor-tipo-doc", leer(persona, ["idTipoIdentidad"]));
  setValor("proveedor-documento", leer(persona, ["nDocumento"]));
  setValor("proveedor-nombre", leer(persona, ["nombre"]));
  setValor("proveedor-ap-paterno", leer(persona, ["apPaterno"]));
  setValor("proveedor-ap-materno", leer(persona, ["apMaterno"]));
  setValor("proveedor-nacimiento", leer(persona, ["fNacimiento"]));
  setValor("proveedor-genero", leer(persona, ["genero"]));
  setValor("proveedor-celular", leer(persona, ["celular"]));
  setValor("proveedor-email", leer(persona, ["email"]));
  setValor("proveedor-direccion", leer(persona, ["direccion"]));
  setValor("proveedor-codigo", leer(proveedor, ["codigoProveedor"]));
  setValor("proveedor-contacto", leer(proveedor, ["contacto"]));
  setValor(
    "proveedor-telefono-contacto",
    leer(proveedor, ["telefonoContacto"])
  );
  setValor("proveedor-email-contacto", leer(proveedor, ["emailContacto"]));
  setValor("proveedor-dias-credito", leer(proveedor, ["diasCredito"]));
}

async function abrirNuevo() {
  editando = null;
  form.reset();
  document.getElementById("proveedor-tipo-doc").value = "";
  titulo.textContent = "Nuevo Proveedor";
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
  const proveedor = proveedores.get(id);
  if (!proveedor) {
    return;
  }
  editando = id;
  titulo.textContent = "Editar Proveedor";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
  try {
    await cargarCatalogos();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudieron cargar los tipos de documento.");
    }
  }
  llenarFormulario(proveedor);
}

async function guardarProveedor() {
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
      await api("POST", "/api/proveedores", payload);
    } else {
      await api("PUT", "/api/proveedores/" + editando, payload);
    }
    form.reset();
    editando = null;
    bootstrap.Modal.getOrCreateInstance(modal).hide();
    await listarProveedores();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudo guardar el proveedor.");
    }
  } finally {
    btnGuardar.disabled = false;
  }
}

async function cambiarEstado(id) {
  const proveedor = proveedores.get(id);
  if (!proveedor) {
    return;
  }
  const activo = estaActivo(proveedor);
  const accion = activo ? "Desactivar" : "Activar";
  if (!window.confirm(accion + " este proveedor?")) {
    return;
  }
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    await api("PATCH", "/api/proveedores/" + id + "/estado", {
      estado: activo ? "0" : "1",
    });
    await listarProveedores();
  } catch (err) {
    if (!irALogin(err)) {
      window.alert(err.mensaje || "No se pudo cambiar el estado.");
    }
  }
}

/* ---------- Eventos ---------- */

btnNuevo.addEventListener("click", abrirNuevo);
btnGuardar.addEventListener("click", guardarProveedor);
inputBuscar.addEventListener("input", renderProveedores);
filtroCredito.addEventListener("change", renderProveedores);

if (inputDocumentoProveedor) {
  inputDocumentoProveedor.addEventListener("input", () => {
    clearTimeout(autocompletarProveedorTimer);
    autocompletarProveedorTimer = setTimeout(() => {
      autocompletarDesdeDocumentoProveedor();
    }, 500);
  });
}

if (selectTipoDocProveedor) {
  selectTipoDocProveedor.addEventListener("change", () => {
    clearTimeout(autocompletarProveedorTimer);
    autocompletarProveedorTimer = setTimeout(() => {
      autocompletarDesdeDocumentoProveedor();
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
  await listarProveedores();
}

iniciar();

if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-proveedores", listarProveedores);
}

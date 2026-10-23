import { api, tieneCsrf, yo } from "../login/login.js";
import { FACTILIZA_API_BASE, FACTILIZA_API_TOKEN } from "../assets/config.js";

const modal = document.getElementById("modalNuevoEmpleado");
const form = document.getElementById("formEmpleado");
const titulo = document.getElementById("modalNuevoEmpleadoLabel");
const alerta = document.getElementById("empleado-alerta");
const btnGuardar = modal.querySelector(".modal-footer .btn-primary");
const btnNuevo = document.getElementById("btn-nuevo-empleado");
const tbody = document.querySelector("#pane-empleados tbody");
const inputBuscar = document.getElementById("empleados-buscar");
const filtroCargo = document.getElementById("filtro-empleados-cargo");
const filtroEstado = document.getElementById("filtro-empleados-estado");
const inputDocumentoEmpleado = document.getElementById("empleado-documento");
const selectTipoDocEmpleado = document.getElementById("empleado-tipo-doc");

const tiposIdentidad = new Map();
const empleados = new Map();
const listaEmpleados = [];

let catalogosPromise = null;
let editando = null;

const CATALOGOS = [
  {
    ruta: "/api/tipos-identidad",
    select: "empleado-tipo-doc",
    id: ["idTipoIdentidad", "id"],
    texto: ["nTipoIdentidad", "nombre"],
  },
  {
    ruta: "/api/cargos",
    select: "empleado-cargo",
    filtro: "filtro-empleados-cargo",
    id: ["idCargo", "id"],
    texto: ["nCargo", "nombre"],
  },
  {
    ruta: "/api/contratos",
    select: "empleado-contrato",
    id: ["idContrato", "id"],
    texto: ["nContrato", "nombre"],
  },
];

function leer(obj, claves) {
  if (!obj) {
    return undefined;
  }
  const clave = claves.find((c) => obj[c] !== undefined && obj[c] !== null);
  return clave === undefined ? undefined : obj[clave];
}

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

async function autocompletarDesdeDocumento(contexto) {
  if (!inputDocumentoEmpleado || !selectTipoDocEmpleado) {
    return;
  }
  const doc = inputDocumentoEmpleado.value.trim();
  if (!doc) {
    return;
  }
  const textoTipo = selectTipoDocEmpleado.options[selectTipoDocEmpleado.selectedIndex]?.textContent?.trim() || "";
  let info = null;
  if (esDni(textoTipo)) {
    info = await consultarFactiliza("dni", doc);
    if (info) {
      if (!document.getElementById("empleado-nombre").value.trim()) {
        setValor("empleado-nombre", info.nombres || "");
      }
      if (!document.getElementById("empleado-ap-paterno").value.trim()) {
        setValor("empleado-ap-paterno", info.apellido_paterno || "");
      }
      if (!document.getElementById("empleado-ap-materno").value.trim()) {
        setValor("empleado-ap-materno", info.apellido_materno || "");
      }
      if (!document.getElementById("empleado-direccion").value.trim() && info.direccion_completa) {
        setValor("empleado-direccion", info.direccion_completa);
      } else if (!document.getElementById("empleado-direccion").value.trim() && info.direccion) {
        setValor("empleado-direccion", info.direccion);
      }
    }
  } else if (esRuc(textoTipo)) {
    info = await consultarFactiliza("ruc", doc);
    if (info) {
      if (!document.getElementById("empleado-nombre").value.trim()) {
        setValor("empleado-nombre", info.nombre_o_razon_social || "");
      }
      if (!document.getElementById("empleado-direccion").value.trim() && info.direccion_completa) {
        setValor("empleado-direccion", info.direccion_completa);
      } else if (!document.getElementById("empleado-direccion").value.trim() && info.direccion) {
        setValor("empleado-direccion", info.direccion);
      }
    }
  }
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

function llenarSelect(select, items, catalogo) {
  const placeholder = select.querySelector('option[value=""]');
  select.replaceChildren(placeholder);
  items.forEach((item) => {
    const opcion = document.createElement("option");
    opcion.value = leer(item, catalogo.id);
    opcion.textContent = aTexto(leer(item, catalogo.texto));
    select.appendChild(opcion);
  });
}

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

async function cargarCatalogo(catalogo) {
  const select = document.getElementById(catalogo.select);
  try {
    const items = (await api("GET", catalogo.ruta)) || [];
    llenarSelect(select, items, catalogo);
    if (catalogo.select === "empleado-tipo-doc") {
      tiposIdentidad.clear();
      items.forEach((item) => {
        tiposIdentidad.set(
          String(leer(item, catalogo.id)),
          aTexto(leer(item, catalogo.texto))
        );
      });
    }
    if (catalogo.filtro) {
      const filtro = document.getElementById(catalogo.filtro);
      if (filtro) {
        llenarSelect(filtro, items, catalogo);
      }
    }
    return true;
  } catch (err) {
    if (!irALogin(err)) {
      console.error("No se pudo cargar " + catalogo.ruta, err.mensaje);
    }
    return false;
  }
}

function cargarCatalogos() {
  if (!catalogosPromise) {
    catalogosPromise = Promise.all(CATALOGOS.map(cargarCatalogo)).then(
      (resultados) => {
        if (!resultados.every(Boolean)) {
          catalogosPromise = null;
        }
        return resultados.every(Boolean);
      }
    );
  }
  return catalogosPromise;
}

/* ---------- Listado ---------- */

function celda(texto) {
  const td = document.createElement("td");
  td.textContent = texto;
  return td;
}

function nombreEmpleado(e) {
  const persona = e.persona || e;
  return (
    leer(e, ["nombreCompleto"]) ||
    [
      leer(persona, ["nombre"]),
      leer(persona, ["apPaterno"]),
      leer(persona, ["apMaterno"]),
    ]
      .filter(Boolean)
      .join(" ")
  );
}

function documentoTexto(persona) {
  const tipo = tiposIdentidad.get(String(leer(persona, ["idTipoIdentidad"])));
  return [tipo, leer(persona, ["nDocumento"])].filter(Boolean).join(" ");
}

function estaActivo(registro) {
  return registro.estado !== false && registro.estado !== "0";
}

function filaEmpleado(e, indice) {
  const persona = e.persona || {};
  const activo = estaActivo(e);
  const id = e.idEmpleado;
  const tr = document.createElement("tr");

  tr.appendChild(celda(id ?? indice + 1));
  tr.appendChild(celda(documentoTexto(persona)));
  tr.appendChild(celda(nombreEmpleado(e)));
  tr.appendChild(celda(aTexto(leer(e, ["nCargo", "cargo"]))));
  tr.appendChild(celda(aTexto(leer(e, ["turno"]))));
  tr.appendChild(celda(aTexto(leer(persona, ["celular"]))));

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
    '<button class="btn btn-sm btn-outline-primary" data-accion="editar" data-id="' +
    id +
    '" title="Editar"><i class="bi bi-pencil"></i></button>' +
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

function textoBusquedaEmpleado(empleado) {
  const persona = empleado.persona || {};
  return normalizarBusqueda(
    [
      documentoTexto(persona),
      leer(persona, ["nDocumento"]),
      nombreEmpleado(empleado),
      leer(empleado, ["nCargo", "cargo"]),
      leer(empleado, ["turno"]),
      leer(persona, ["celular"]),
      leer(persona, ["email"]),
    ]
      .filter(Boolean)
      .join(" ")
  );
}

function renderEmpleados() {
  const termino = normalizarBusqueda(inputBuscar?.value);
  const cargo = filtroCargo?.value || "";
  const estado = filtroEstado?.value || "";
  const visibles = listaEmpleados.filter((empleado) => {
    const coincideTexto =
      !termino || textoBusquedaEmpleado(empleado).includes(termino);
    const coincideCargo =
      !cargo || String(leer(empleado, ["idCargo"]) ?? "") === cargo;
    const coincideEstado =
      !estado || String(estaActivo(empleado) ? "1" : "0") === estado;
    return coincideTexto && coincideCargo && coincideEstado;
  });

  if (!listaEmpleados.length) {
    tbody.replaceChildren(filaMensaje("No hay empleados registrados."));
    return;
  }
  if (!visibles.length) {
    tbody.replaceChildren(filaMensaje("No se encontraron empleados."));
    return;
  }
  tbody.replaceChildren(...visibles.map(filaEmpleado));
}

async function listarEmpleados() {
  try {
    const lista = (await api("GET", "/api/empleados")) || [];
    listaEmpleados.length = 0;
    empleados.clear();
    lista.forEach((empleado) => {
      listaEmpleados.push(empleado);
      empleados.set(empleado.idEmpleado, empleado);
    });
    renderEmpleados();
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
      idDistrito: numeroONulo("empleado-distrito"),
      idTipoIdentidad: numeroONulo("empleado-tipo-doc"),
      nDocumento: valor("empleado-documento"),
      nombre: valor("empleado-nombre"),
      apPaterno: textoONulo("empleado-ap-paterno"),
      apMaterno: textoONulo("empleado-ap-materno"),
      fNacimiento: textoONulo("empleado-nacimiento"),
      email: textoONulo("empleado-email"),
      celular: textoONulo("empleado-celular"),
      genero: textoONulo("empleado-genero"),
      direccion: textoONulo("empleado-direccion"),
    },
    idContrato: numeroONulo("empleado-contrato"),
    idCargo: numeroONulo("empleado-cargo"),
    salario: numeroONulo("empleado-salario"),
    turno: textoONulo("empleado-turno"),
    fondoPension: textoONulo("empleado-fondo-pension"),
    nHps: textoONulo("empleado-hps"),
    essalud: textoONulo("empleado-essalud"),
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
    const selectTipo = document.getElementById("empleado-tipo-doc");
    const textoTipo =
      selectTipo.options[selectTipo.selectedIndex]?.textContent?.trim() || "";
    const longitud = tiposIdentidad.get(String(payload.persona.idTipoIdentidad));
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

function llenarFormulario(empleado) {
  const persona = empleado.persona || {};
  form.reset();

  const idTipo = leer(persona, ["idTipoIdentidad"]);
  agregarOpcionFaltante(
    document.getElementById("empleado-tipo-doc"),
    idTipo,
    tiposIdentidad.get(String(idTipo)) || "Tipo " + aTexto(idTipo)
  );
  setValor("empleado-tipo-doc", idTipo);
  setValor("empleado-documento", leer(persona, ["nDocumento"]));
  setValor("empleado-nombre", leer(persona, ["nombre"]));
  setValor("empleado-ap-paterno", leer(persona, ["apPaterno"]));
  setValor("empleado-ap-materno", leer(persona, ["apMaterno"]));
  setValor("empleado-nacimiento", leer(persona, ["fNacimiento"]));
  setValor("empleado-genero", leer(persona, ["genero"]));
  setValor("empleado-celular", leer(persona, ["celular"]));
  setValor("empleado-email", leer(persona, ["email"]));
  setValor("empleado-direccion", leer(persona, ["direccion"]));
  setValor("empleado-distrito", leer(persona, ["idDistrito"]));

  const idCargo = leer(empleado, ["idCargo"]);
  agregarOpcionFaltante(
    document.getElementById("empleado-cargo"),
    idCargo,
    leer(empleado, ["nCargo"]) || "Cargo " + aTexto(idCargo)
  );
  setValor("empleado-cargo", idCargo);

  const idContrato = leer(empleado, ["idContrato"]);
  agregarOpcionFaltante(
    document.getElementById("empleado-contrato"),
    idContrato,
    leer(empleado, ["nContrato"]) || "Contrato " + aTexto(idContrato)
  );
  setValor("empleado-contrato", idContrato);

  setValor("empleado-turno", leer(empleado, ["turno"]));
  setValor("empleado-salario", leer(empleado, ["salario"]));
  setValor("empleado-fondo-pension", leer(empleado, ["fondoPension"]));
  setValor("empleado-hps", leer(empleado, ["nHps"]));
  setValor("empleado-essalud", leer(empleado, ["essalud"]));
}

async function cargarFormularioEdicion() {
  try {
    await cargarCatalogos();
  } catch (err) {
    // Los catálogos ya muestran su propio error.
  }
  if (editando === null) {
    return;
  }
  const empleado = empleados.get(editando);
  if (empleado) {
    llenarFormulario(empleado);
  }
}

async function abrirNuevo() {
  editando = null;
  form.reset();
  document.getElementById("empleado-tipo-doc").value = "";
  titulo.textContent = "Nuevo Empleado";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
  await cargarCatalogos();
}

async function abrirEditar(id) {
  const empleado = empleados.get(id);
  if (!empleado) {
    return;
  }
  editando = id;
  titulo.textContent = "Editar Empleado";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
  await cargarFormularioEdicion();
}

async function guardarEmpleado() {
  limpiarError();
  const payload = armarPayload();
  const errores = validar(payload);
  if (errores.length) {
    mostrarError(errores.join(" "));
    return;
  }

  const eraEdicion = editando !== null;
  btnGuardar.disabled = true;
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    if (eraEdicion) {
      await api("PUT", "/api/empleados/" + editando, payload);
    } else {
      await api("POST", "/api/empleados", payload);
    }
    form.reset();
    editando = null;
    bootstrap.Modal.getOrCreateInstance(modal).hide();
    await listarEmpleados();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(err.mensaje || "No se pudo guardar el empleado.");
    }
  } finally {
    btnGuardar.disabled = false;
  }
}

async function cambiarEstado(id) {
  const empleado = empleados.get(id);
  if (!empleado) {
    return;
  }
  const activo = estaActivo(empleado);
  const accion = activo ? "Desactivar" : "Activar";
  if (!window.confirm(accion + " este empleado?")) {
    return;
  }
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    await api("PATCH", "/api/empleados/" + id + "/estado", {
      estado: activo ? "0" : "1",
    });
    await listarEmpleados();
  } catch (err) {
    if (!irALogin(err)) {
      window.alert(err.mensaje || "No se pudo cambiar el estado.");
    }
  }
}

/* ---------- Eventos ---------- */

btnNuevo.addEventListener("click", abrirNuevo);
btnGuardar.addEventListener("click", guardarEmpleado);
inputBuscar.addEventListener("input", renderEmpleados);
filtroCargo?.addEventListener("change", renderEmpleados);
filtroEstado?.addEventListener("change", renderEmpleados);

if (inputDocumentoEmpleado) {
  inputDocumentoEmpleado.addEventListener("input", () => {
    clearTimeout(inputDocumentoEmpleado._factilizaTimer);
    inputDocumentoEmpleado._factilizaTimer = setTimeout(() => {
      autocompletarDesdeDocumento();
    }, 500);
  });
}

if (selectTipoDocEmpleado) {
  selectTipoDocEmpleado.addEventListener("change", () => {
    clearTimeout(selectTipoDocEmpleado._factilizaTimer);
    selectTipoDocEmpleado._factilizaTimer = setTimeout(() => {
      autocompletarDesdeDocumento();
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

modal.addEventListener("hidden.bs.modal", () => {
  editando = null;
  form.reset();
  limpiarError();
});

async function iniciar() {
  await cargarCatalogos();
  await listarEmpleados();
}

iniciar();

if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-empleados", listarEmpleados);
}

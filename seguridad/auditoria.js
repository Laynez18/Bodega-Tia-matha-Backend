import { api } from "../login/login.js";

const alerta = document.getElementById("auditoria-alerta");
const tbody = document.getElementById("auditoria-tabla");
const selectUsuario = document.getElementById("auditoriaUsuario");
const selectTabla = document.getElementById("auditoriaTabla");
const selectAccion = document.getElementById("auditoriaAccion");
const inputDesde = document.getElementById("auditoriaDesde");
const inputHasta = document.getElementById("auditoriaHasta");
const inputTexto = document.getElementById("auditoriaTexto");
const btnBuscar = document.getElementById("auditoria-buscar");
const btnLimpiar = document.getElementById("auditoria-limpiar");
const modalDetalle = document.getElementById("modalDetalleAuditoria");

const ETIQUETAS_ACCION = {
  INSERT: "Registro",
  UPDATE: "Actualizacion",
  DELETE: "Eliminacion",
  ACTIVAR: "Activacion",
  DESACTIVAR: "Desactivacion",
  PERMISO_CONCEDER: "Permiso concedido",
  PERMISO_REVOCAR: "Permiso revocado",
};

let registros = [];
let solicitudActual = 0;
let temporizadorBusqueda = null;

function aTexto(valor) {
  return valor === undefined || valor === null ? "" : String(valor);
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

function celda(texto) {
  const td = document.createElement("td");
  td.textContent = texto;
  return td;
}

function filaMensaje(texto) {
  const tr = document.createElement("tr");
  const td = celda(texto);
  td.colSpan = 8;
  td.className = "text-center text-muted py-4";
  tr.appendChild(td);
  return tr;
}

function opcion(valor, texto) {
  const option = document.createElement("option");
  option.value = valor;
  option.textContent = texto;
  return option;
}

function nombreUsuario(auditoria) {
  if (auditoria.nombreUsuario) {
    return auditoria.nombreUsuario;
  }
  return auditoria.usuario || "Sistema";
}

function textoAccion(accion) {
  return ETIQUETAS_ACCION[accion] || accion || "";
}

function claseAccion(accion) {
  switch (accion) {
    case "INSERT":
    case "ACTIVAR":
      return "text-bg-success";
    case "UPDATE":
      return "text-bg-primary";
    case "DELETE":
    case "DESACTIVAR":
      return "text-bg-danger";
    case "PERMISO_CONCEDER":
      return "text-bg-info";
    case "PERMISO_REVOCAR":
      return "text-bg-warning";
    default:
      return "text-bg-secondary";
  }
}

function fechaTexto(valor) {
  if (!valor) {
    return "";
  }
  const fecha = new Date(valor);
  if (Number.isNaN(fecha.getTime())) {
    return aTexto(valor);
  }
  return fecha.toLocaleString("es-PE", {
    dateStyle: "short",
    timeStyle: "medium",
  });
}

function resumirDetalle(auditoria) {
  const valor = auditoria.valorNuevo || auditoria.valorAnterior || "";
  return valor.length > 120 ? valor.slice(0, 117) + "..." : valor;
}

function celdaUsuario(auditoria) {
  const td = document.createElement("td");
  const nombre = nombreUsuario(auditoria);
  const logeo = auditoria.usuario;
  if (logeo && logeo !== nombre) {
    const principal = document.createElement("div");
    principal.textContent = nombre;
    const secundario = document.createElement("small");
    secundario.className = "text-muted d-block";
    secundario.textContent = logeo;
    td.append(principal, secundario);
  } else {
    td.textContent = nombre;
  }
  return td;
}

function celdaAccion(auditoria) {
  const td = document.createElement("td");
  const badge = document.createElement("span");
  badge.className = "badge " + claseAccion(auditoria.accion);
  badge.textContent = textoAccion(auditoria.accion);
  td.appendChild(badge);
  return td;
}

function celdaVer(auditoria) {
  const td = document.createElement("td");
  td.className = "text-end";
  const boton = document.createElement("button");
  boton.type = "button";
  boton.className = "btn btn-sm btn-outline-primary";
  boton.title = "Ver detalle";
  boton.dataset.id = auditoria.idAuditoria;
  boton.innerHTML = '<i class="bi bi-eye"></i>';
  td.appendChild(boton);
  return td;
}

function filaAuditoria(auditoria) {
  const tr = document.createElement("tr");
  const equipo = [auditoria.ip, auditoria.terminal].filter(Boolean).join(" / ");
  tr.append(
    celda(fechaTexto(auditoria.fEvento)),
    celdaUsuario(auditoria),
    celda(aTexto(auditoria.nTabla)),
    celdaAccion(auditoria),
    celda(aTexto(auditoria.idRegistro)),
    celda(resumirDetalle(auditoria)),
    celda(equipo),
    celdaVer(auditoria),
  );
  return tr;
}

function llenarSelect(select, valorTodos, etiquetaTodos, valores) {
  const anterior = select.value;
  select.replaceChildren(opcion("", etiquetaTodos));
  valores.forEach((valor) => {
    const texto = typeof valor === "object" ? valor.texto : aTexto(valor);
    const dato = typeof valor === "object" ? valor.valor : valor;
    select.appendChild(opcion(aTexto(dato), texto));
  });
  if ([...select.options].some((option) => option.value === anterior)) {
    select.value = anterior;
  } else {
    select.value = valorTodos;
  }
}

async function cargarFiltros() {
  const datos = (await api("GET", "/api/auditoria/filtros")) || {};
  const usuarios = (datos.usuarios || []).map((usuario) => ({
    valor: usuario.idUsuario,
    texto: usuario.nombreEmpleado
      ? `${usuario.logeo} - ${usuario.nombreEmpleado}`
      : aTexto(usuario.logeo),
  }));
  llenarSelect(selectUsuario, "", "Todos los usuarios", usuarios);
  llenarSelect(selectTabla, "", "Todas las tablas", datos.tablas || []);
  llenarSelect(selectAccion, "", "Todas las acciones", datos.acciones || []);
}

function construirConsulta() {
  const params = new URLSearchParams();
  if (selectUsuario.value) {
    params.set("idUsuario", selectUsuario.value);
  }
  if (selectTabla.value) {
    params.set("nTabla", selectTabla.value);
  }
  if (selectAccion.value) {
    params.set("accion", selectAccion.value);
  }
  if (inputDesde.value) {
    params.set("desde", inputDesde.value);
  }
  if (inputHasta.value) {
    params.set("hasta", inputHasta.value);
  }
  const texto = inputTexto.value.trim();
  if (texto) {
    params.set("texto", texto);
  }
  const consulta = params.toString();
  return consulta ? "?" + consulta : "";
}

async function listarAuditoria() {
  limpiarError();
  if (inputDesde.value && inputHasta.value && inputDesde.value > inputHasta.value) {
    mostrarError("La fecha desde no puede ser mayor que la fecha hasta.");
    tbody.replaceChildren(filaMensaje("Corrige el rango de fechas."));
    return;
  }

  const numeroSolicitud = ++solicitudActual;
  tbody.replaceChildren(filaMensaje("Cargando registros..."));
  try {
    const datos = (await api("GET", "/api/auditoria" + construirConsulta())) || [];
    if (numeroSolicitud !== solicitudActual) {
      return;
    }
    registros = datos;
    if (!registros.length) {
      tbody.replaceChildren(filaMensaje("No hay registros de auditoria."));
      return;
    }
    tbody.replaceChildren(...registros.map(filaAuditoria));
  } catch (err) {
    if (numeroSolicitud !== solicitudActual) {
      return;
    }
    if (!irALogin(err)) {
      tbody.replaceChildren(filaMensaje("No se pudo cargar la auditoria."));
      mostrarError(err.mensaje || "No se pudo cargar la auditoria.");
    }
  }
}

function escribirDetalle(id, valor) {
  document.getElementById(id).textContent = valor || "Sin datos.";
}

function abrirDetalle(auditoria) {
  document.getElementById("auditoria-detalle-id").textContent =
    aTexto(auditoria.idAuditoria);
  document.getElementById("auditoria-detalle-fecha").textContent = fechaTexto(
    auditoria.fEvento,
  );
  document.getElementById("auditoria-detalle-usuario").textContent =
    auditoria.nombreUsuario
      ? `${nombreUsuario(auditoria)} (${auditoria.usuario || "sin logeo"})`
      : nombreUsuario(auditoria);
  document.getElementById("auditoria-detalle-tabla").textContent = aTexto(
    auditoria.nTabla,
  );
  document.getElementById("auditoria-detalle-accion").textContent = textoAccion(
    auditoria.accion,
  );
  document.getElementById("auditoria-detalle-registro").textContent =
    aTexto(auditoria.idRegistro) || "Sin registro asociado";
  document.getElementById("auditoria-detalle-ip").textContent =
    aTexto(auditoria.ip) || "No registrada";
  document.getElementById("auditoria-detalle-terminal").textContent =
    aTexto(auditoria.terminal) || "No registrado";
  escribirDetalle("auditoria-detalle-anterior", auditoria.valorAnterior);
  escribirDetalle("auditoria-detalle-nuevo", auditoria.valorNuevo);
  bootstrap.Modal.getOrCreateInstance(modalDetalle).show();
}

function limpiarFiltros() {
  selectUsuario.value = "";
  selectTabla.value = "";
  selectAccion.value = "";
  inputDesde.value = "";
  inputHasta.value = "";
  inputTexto.value = "";
  listarAuditoria();
}

btnBuscar?.addEventListener("click", listarAuditoria);
btnLimpiar?.addEventListener("click", limpiarFiltros);
[selectUsuario, selectTabla, selectAccion, inputDesde, inputHasta].forEach(
  (control) => control?.addEventListener("change", listarAuditoria),
);
inputTexto?.addEventListener("input", () => {
  if (temporizadorBusqueda) {
    clearTimeout(temporizadorBusqueda);
  }
  temporizadorBusqueda = setTimeout(listarAuditoria, 250);
});

tbody.addEventListener("click", (evento) => {
  const boton = evento.target.closest("button[data-id]");
  if (!boton) {
    return;
  }
  const auditoria = registros.find(
    (registro) => String(registro.idAuditoria) === boton.dataset.id,
  );
  if (auditoria) {
    abrirDetalle(auditoria);
  }
});

async function iniciar() {
  try {
    await cargarFiltros();
  } catch (err) {
    if (!irALogin(err)) {
      mostrarError(
        err.mensaje || "No se pudieron cargar los filtros de auditoria.",
      );
    }
  }
  await listarAuditoria();
}

iniciar();
if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-auditoria", iniciar);
}

import { api } from "../login/login.js";

const tabla = document.getElementById("tablaPermisos");
const encabezado = tabla.tHead.rows[0];
const tbody = tabla.tBodies[0];
const inputBuscar = document.getElementById("permisos-buscar");
const filtroModulo = document.getElementById("filtro-permisos-modulo");

let matrizActual = null;

function normalizarBusqueda(valor) {
  return String(valor ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function celda(tag, texto, clase) {
  const el = document.createElement(tag);
  el.textContent = texto;
  if (clase) {
    el.className = clase;
  }
  return el;
}

function celdaPermiso(permiso) {
  const td = document.createElement("td");
  const clave = document.createElement("span");
  clave.className = "text-muted small";
  clave.textContent = permiso.clave;
  td.append(clave, " " + permiso.nPermiso);
  return td;
}

function celdaSwitch(idRol, permiso) {
  const td = document.createElement("td");
  td.className = "text-center";

  const contenedor = document.createElement("div");
  contenedor.className = "form-check form-switch d-inline-flex";

  const input = document.createElement("input");
  input.className = "form-check-input";
  input.type = "checkbox";
  input.role = "switch";
  input.checked = Boolean(permiso.concedidoPorRol[String(idRol)]);
  input.dataset.idRol = idRol;
  input.dataset.idPermiso = permiso.idPermiso;

  contenedor.appendChild(input);
  td.appendChild(contenedor);
  return td;
}

function filaModulo(modulo, columnas) {
  const tr = document.createElement("tr");
  tr.className = "table-secondary";
  const td = document.createElement("td");
  td.colSpan = columnas;
  const strong = document.createElement("strong");
  strong.textContent = modulo.nModulo;
  td.appendChild(strong);
  tr.appendChild(td);
  return tr;
}

function filaMensaje(texto, columnas) {
  const tr = document.createElement("tr");
  const td = celda("td", texto, "text-center text-muted");
  td.colSpan = columnas;
  tr.appendChild(td);
  return tr;
}

function pintar(matriz) {
  matrizActual = matriz;
  pintarFiltroModulos(matriz.modulos || []);
  renderMatriz();
}

function pintarFiltroModulos(modulos) {
  const actual = filtroModulo.value;
  const opciones = [new Option("Todos los módulos", "")];
  modulos.forEach((modulo) => {
    opciones.push(new Option(modulo.nModulo, String(modulo.idModulo)));
  });
  filtroModulo.replaceChildren(...opciones);
  filtroModulo.value = [...filtroModulo.options].some(
    (opcion) => opcion.value === actual,
  )
    ? actual
    : "";
}

function renderMatriz() {
  if (!matrizActual) {
    return;
  }
  const roles = matrizActual.roles || [];
  const columnas = roles.length + 1;
  const termino = normalizarBusqueda(inputBuscar?.value);
  const moduloSeleccionado = filtroModulo?.value || "";

  encabezado.replaceChildren(
    celda("th", "Permiso / Rol"),
    ...roles.map((rol) => celda("th", rol.nRol, "text-center")),
  );

  const filas = [];
  (matrizActual.modulos || []).forEach((modulo) => {
    if (
      moduloSeleccionado &&
      String(modulo.idModulo) !== moduloSeleccionado
    ) {
      return;
    }
    const moduloCoincide =
      !termino || normalizarBusqueda(modulo.nModulo).includes(termino);
    const permisos = (modulo.permisos || []).filter((permiso) => {
      if (!termino || moduloCoincide) {
        return true;
      }
      return normalizarBusqueda(
        `${permiso.clave ?? ""} ${permiso.nPermiso ?? ""}`,
      ).includes(termino);
    });
    if (!permisos.length) {
      return;
    }

    filas.push(filaModulo(modulo, columnas));
    permisos.forEach((permiso) => {
      const tr = document.createElement("tr");
      tr.append(
        celdaPermiso(permiso),
        ...roles.map((rol) => celdaSwitch(rol.idRol, permiso)),
      );
      filas.push(tr);
    });
  });

  tbody.replaceChildren(
    ...(filas.length ? filas : [filaMensaje("No se encontraron permisos.", columnas)]),
  );
}

export async function cargarMatriz() {
  try {
    pintar(await api("GET", "/api/permisos/matriz"));
  } catch (err) {
    if (err.status === 401) {
      window.location.href = "../login/login.html";
      return;
    }
    tbody.replaceChildren(
      filaMensaje(err.mensaje || "No se pudo cargar.", encabezado.cells.length),
    );
  }
}

cargarMatriz();
inputBuscar.addEventListener("input", renderMatriz);
filtroModulo.addEventListener("change", renderMatriz);
if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-permisos", cargarMatriz);
}

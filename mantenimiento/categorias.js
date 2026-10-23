import { api, tieneCsrf, yo } from "../login/login.js";

const POR_PAGINA = 10;

const tbody = document.querySelector("#tablaCategorias tbody");
const inputBuscar = document.getElementById("categorias-buscar");
const filtroEstado = document.getElementById("categorias-filtro-estado");
const btnNuevo = document.getElementById("btn-nueva-categoria");
const infoPaginacion = document.getElementById("categorias-paginacion-info");
const paginacion = document.getElementById("categorias-paginacion");

const modal = document.getElementById("modalCategoria");
const form = document.getElementById("formCategoria");
const titulo = document.getElementById("modalCategoriaLabel");
const alerta = document.getElementById("categoria-alerta");
const btnGuardar = document.getElementById("btn-guardar-categoria");
const inputNombre = document.getElementById("categoria-nombre");
const inputDescripcion = document.getElementById("categoria-descripcion");

let categorias = [];
let pagina = 0;
let totalRegistros = 0;
let editando = null;
let temporizador = null;

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

function normalizar(valor) {
  return aTexto(valor)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function estaActivo(registro) {
  return registro.estado !== false && registro.estado !== "0";
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
  td.textContent = texto === undefined || texto === null ? "" : texto;
  return td;
}

function filaMensaje(texto) {
  const tr = document.createElement("tr");
  const td = document.createElement("td");
  td.colSpan = 5;
  td.className = "text-center text-muted";
  td.textContent = texto;
  tr.appendChild(td);
  return tr;
}

function renderPaginacion() {
  const totalPaginas = Math.max(1, Math.ceil(totalRegistros / POR_PAGINA));
  const inicio = totalRegistros ? pagina * POR_PAGINA + 1 : 0;
  const fin = Math.min(totalRegistros, (pagina + 1) * POR_PAGINA);
  if (infoPaginacion) {
    infoPaginacion.textContent = totalRegistros
      ? `Mostrando ${inicio} - ${fin} de ${totalRegistros}`
      : "Sin resultados";
  }

  if (!paginacion) {
    return;
  }
  paginacion.replaceChildren();
  const anterior = document.createElement("li");
  anterior.className = "page-item" + (pagina === 0 ? " disabled" : "");
  const btnAnt = document.createElement("button");
  btnAnt.className = "page-link";
  btnAnt.type = "button";
  btnAnt.textContent = "«";
  btnAnt.disabled = pagina === 0;
  btnAnt.addEventListener("click", () => {
    if (pagina > 0) {
      pagina -= 1;
      render();
    }
  });
  anterior.appendChild(btnAnt);
  paginacion.appendChild(anterior);

  const maxVisibles = 5;
  let desde = Math.max(0, pagina - Math.floor(maxVisibles / 2));
  let hasta = Math.min(totalPaginas, desde + maxVisibles);
  if (hasta - desde < maxVisibles) {
    desde = Math.max(0, hasta - maxVisibles);
  }
  for (let i = desde; i < hasta; i += 1) {
    const li = document.createElement("li");
    li.className = "page-item" + (i === pagina ? " active" : "");
    const btn = document.createElement("button");
    btn.className = "page-link";
    btn.type = "button";
    btn.textContent = String(i + 1);
    btn.addEventListener("click", () => {
      pagina = i;
      render();
    });
    li.appendChild(btn);
    paginacion.appendChild(li);
  }

  const siguiente = document.createElement("li");
  siguiente.className = "page-item" + (pagina >= totalPaginas - 1 ? " disabled" : "");
  const btnSig = document.createElement("button");
  btnSig.className = "page-link";
  btnSig.type = "button";
  btnSig.textContent = "»";
  btnSig.disabled = pagina >= totalPaginas - 1;
  btnSig.addEventListener("click", () => {
    if (pagina < totalPaginas - 1) {
      pagina += 1;
      render();
    }
  });
  siguiente.appendChild(btnSig);
  paginacion.appendChild(siguiente);
}

function aplicarFiltros() {
  const termino = normalizar(inputBuscar?.value || "");
  const estado = filtroEstado?.value || "";
  let lista = categorias.slice();
  if (termino) {
    lista = lista.filter((c) => {
      const texto = normalizar(
        aTexto(leer(c, ["nCategoriaProducto", "nombre"])) +
          " " +
          aTexto(leer(c, ["descripcion"]))
      );
      return texto.includes(termino);
    });
  }
  if (estado) {
    lista = lista.filter((c) => (estado === "1" ? estaActivo(c) : !estaActivo(c)));
  }
  totalRegistros = lista.length;
  const totalPaginas = Math.max(1, Math.ceil(totalRegistros / POR_PAGINA));
  if (pagina >= totalPaginas) {
    pagina = totalPaginas - 1;
  }
  if (pagina < 0) {
    pagina = 0;
  }
  const inicio = pagina * POR_PAGINA;
  return lista.slice(inicio, inicio + POR_PAGINA);
}

function render() {
  const lista = aplicarFiltros();
  tbody.replaceChildren();
  if (!lista.length) {
    tbody.appendChild(filaMensaje("Sin categorías para mostrar."));
    renderPaginacion();
    return;
  }
  lista.forEach((c, indice) => {
    const tr = document.createElement("tr");
    const id = leer(c, ["idCategoriaProducto", "id"]);
    const nombre = aTexto(leer(c, ["nCategoriaProducto", "nombre"]));
    const descripcion = aTexto(leer(c, ["descripcion"]));
    const activo = estaActivo(c);
    tr.appendChild(celda(id ?? indice + 1));
    tr.appendChild(celda(nombre));
    tr.appendChild(celda(descripcion));
    const tdEstado = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = "badge " + (activo ? "text-bg-success" : "text-bg-secondary");
    badge.textContent = activo ? "Activo" : "Inactivo";
    tdEstado.appendChild(badge);
    tr.appendChild(tdEstado);
    const tdAcciones = document.createElement("td");
    const btnEditar = document.createElement("button");
    btnEditar.className = "btn btn-outline-primary btn-sm me-1";
    btnEditar.type = "button";
    btnEditar.dataset.accion = "editar";
    btnEditar.dataset.id = id;
    btnEditar.innerHTML = '<i class="bi bi-pencil"></i>';
    tdAcciones.appendChild(btnEditar);
    const btnEstado = document.createElement("button");
    btnEstado.className = "btn btn-outline-primary btn-sm";
    btnEstado.type = "button";
    btnEstado.dataset.accion = activo ? "desactivar" : "activar";
    btnEstado.dataset.id = id;
    btnEstado.title = activo ? "Desactivar" : "Activar";
    btnEstado.innerHTML = activo ? '<i class="bi bi-toggle-on"></i>' : '<i class="bi bi-toggle-off"></i>';
    tdAcciones.appendChild(btnEstado);
    tr.appendChild(tdAcciones);
    tbody.appendChild(tr);
  });
  renderPaginacion();
}

async function listarCategorias() {
  tbody.replaceChildren(filaMensaje("Cargando categorías..."));
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    categorias = (await api("GET", "/api/categorias")) || [];
    pagina = 0;
    render();
  } catch (err) {
    if (irALogin(err)) {
      return;
    }
    categorias = [];
    totalRegistros = 0;
    tbody.replaceChildren(filaMensaje(err.mensaje || "No se pudieron cargar las categorías."));
    renderPaginacion();
  }
}

function abrirNuevo() {
  editando = null;
  form.reset();
  titulo.textContent = "Nueva Categoría";
  limpiarError();
  bootstrap.Modal.getOrCreateInstance(modal).show();
}

async function abrirEditar(id) {
  const cat = categorias.find((c) => String(leer(c, ["idCategoriaProducto", "id"])) === String(id));
  if (!cat) {
    return;
  }
  editando = cat;
  form.reset();
  titulo.textContent = "Editar Categoría";
  limpiarError();
  inputNombre.value = aTexto(leer(cat, ["nCategoriaProducto", "nombre"]));
  inputDescripcion.value = aTexto(leer(cat, ["descripcion"]));
  bootstrap.Modal.getOrCreateInstance(modal).show();
}

async function guardarCategoria() {
  limpiarError();
  const nombre = inputNombre.value.trim();
  if (!nombre) {
    mostrarError("El nombre es obligatorio.");
    return;
  }
  const payload = {
    nCategoriaProducto: nombre,
    descripcion: inputDescripcion.value.trim() || null,
  };
  btnGuardar.disabled = true;
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    if (editando === null) {
      await api("POST", "/api/categorias", payload);
    } else {
      const id = leer(editando, ["idCategoriaProducto", "id"]);
      await api("PUT", "/api/categorias/" + id, payload);
    }
    form.reset();
    editando = null;
    bootstrap.Modal.getOrCreateInstance(modal).hide();
    await listarCategorias();
  } catch (err) {
    if (irALogin(err)) {
      return;
    }
    mostrarError(err.mensaje || "No se pudo guardar la categoría.");
  } finally {
    btnGuardar.disabled = false;
  }
}

async function cambiarEstado(id, activar) {
  const cat = categorias.find((c) => String(leer(c, ["idCategoriaProducto", "id"])) === String(id));
  if (!cat) {
    return;
  }
  const nombre = aTexto(leer(cat, ["nCategoriaProducto", "nombre"]));
  const pregunta = activar ? `Activar la categoría "${nombre}"?` : `Desactivar la categoría "${nombre}"?`;
  if (!window.confirm(pregunta)) {
    return;
  }
  try {
    if (!tieneCsrf()) {
      await yo();
    }
    await api("PATCH", "/api/categorias/" + id + "/estado", { estado: activar ? "1" : "0" });
    await listarCategorias();
  } catch (err) {
    if (irALogin(err)) {
      return;
    }
    window.alert(err.mensaje || "No se pudo cambiar el estado.");
  }
}

btnNuevo.addEventListener("click", abrirNuevo);
btnGuardar.addEventListener("click", guardarCategoria);

if (inputBuscar) {
  inputBuscar.addEventListener("input", () => {
    clearTimeout(temporizador);
    temporizador = setTimeout(() => {
      pagina = 0;
      render();
    }, 300);
  });
}

if (filtroEstado) {
  filtroEstado.addEventListener("change", () => {
    pagina = 0;
    render();
  });
}

if (tbody) {
  tbody.addEventListener("click", (evento) => {
    const boton = evento.target.closest("button[data-accion]");
    if (!boton) {
      return;
    }
    const id = boton.dataset.id;
    if (boton.dataset.accion === "editar") {
      abrirEditar(id);
    } else if (boton.dataset.accion === "activar") {
      cambiarEstado(id, true);
    } else if (boton.dataset.accion === "desactivar") {
      cambiarEstado(id, false);
    }
  });
}

modal?.addEventListener("hidden.bs.modal", () => {
  editando = null;
  form.reset();
  limpiarError();
});

listarCategorias();
if (window.registrarRecargaPanel) {
  window.registrarRecargaPanel("pane-categorias", listarCategorias);
}
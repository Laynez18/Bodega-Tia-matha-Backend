# Comparación: Bodega-Tia-matha-Frontend-master vs. bodeguita

**Proyecto nuevo (analizado):** `C:\Users\USUARIO\Desktop\Bodega-Tia-matha-Frontend-master`
**Proyecto anterior (referencia):** `C:\Users\USUARIO\Desktop\bodeguita`
**Fecha del análisis:** 2026-10-22

> Nota: `bodeguita` tiene una carpeta `.git`; el proyecto nuevo no. La comparación se hizo ignorando `.git` y los saltos de línea (CRLF), para detectar solo cambios reales de contenido.

---

## Resumen general

El proyecto nuevo **no elimina** archivos del anterior. Sus cambios principales son:

1. **Refactorización del sidebar**: se dejó de repetir el menú lateral en cada HTML y ahora se inyecta desde un único archivo compartido.
2. **Nuevos archivos compartidos** en `assets/` para sidebar, navegación persistente (tipo SPA) y control de sesión.
3. **Nuevos módulos funcionales** `compras/compras.js` y `ventas/ventas.js`.
4. **Mejoras de responsividad móvil** en `assets/global.css`.
5. **Cambio de URL del backend de producción** y verificación automática de sesión.
6. Ajustes menores en `login.html` e `inicio.js`.

---

## Archivos nuevos (no existen en `bodeguita`)

| Archivo | Descripción |
|---|---|
| `assets/sidebar.html` | Sidebar único y compartido. Incluye el menú en 4 grupos tipo acordeón (Contenedor 1 a 4) y el dropdown de usuario con opción **Cerrar sesión**. |
| `assets/inject-sidebar.js` | Carga `sidebar.html` dentro de `#sidebar-contenedor` mediante `fetch`. Marca el enlace activo según la URL, guarda/restaura el estado de los acordeones en `localStorage`, mantiene el scroll del sidebar en `sessionStorage` y aplica ancho fijo de 250px. |
| `assets/navegacion-persistente.js` | Navegación tipo SPA: intercepta los clics de enlaces del sidebar, hace `fetch` de la página destino, reemplaza solo el `<main>` y los modales, ejecuta los scripts de la nueva página y actualiza la URL con `history.pushState`. El sidebar no se recarga. |
| `assets/sesion.js` | Control de inactividad: cierra la sesión tras **15 minutos** sin actividad (`mousemove`, `keydown`, `click`, `scroll`, `touchstart`). Llama a `POST /api/auth/logout`, limpia `mm-usuario-recordado` y redirige al login. Expone `window.cerrarSesion()`. |
| `assets/verificar-sesion.js` | Verifica la sesión con `yo()` al cargar la página y redirige al login si el backend responde `401`. |
| `compras/compras.js` | Nuevo módulo de compras: carga proveedores y productos, permite agregar productos a un carrito, calcula subtotal/IGV (18%)/total, valida stock y registra compras con `POST /api/compras`. También lista las compras existentes. |
| `ventas/ventas.js` | Nuevo módulo de ventas: carga clientes, métodos de pago y productos, gestiona carrito, calcula subtotal/IGV (18%)/total, valida stock y registra ventas con `POST /api/ventas`. También lista las ventas existentes. |

---

## Archivos modificados (con cambios reales de contenido)

### `assets/config.js`
- La URL de producción cambió de:
  - `https://bodeguita-backend.onrender.com`
  - a `https://bodega-tia-matha-backend.onrender.com`
- Se reemplazó `indexOf(...) === 0` por `startsWith(...)` y se ajustó el formato de `API_BASE`.
- Sin cambios funcionales adicionales.

### `assets/global.css`
- Se agregó `#sidebar-contenedor` con ancho fijo (`250px`) para evitar que el sidebar colapse o se desplace al inyectarse.
- Se agregó una sección completa de **responsividad móvil**:
  - Tablas con scroll horizontal táctil.
  - Pestañas `nav-tabs` desplazables.
  - Tarjetas, botones, badges, títulos y formularios más compactos.
  - Botones de acción de tablas en columna.
  - Ajustes para pantallas menores a 576px: tarjetas KPI verticales, modales más estrechos y ocultamiento de columnas poco importantes de tablas.

### `inicio/inicio.js`
- Antes solo hacía `console.log(await yo(), "hola")`.
- Ahora ejecuta una verificación de sesión con manejo de errores:
  - Si el error es `401`, redirige a `../login/login.html`.
  - Si es otro error, lo registra en consola.

### `login/login.html`
- El placeholder del campo contraseña cambió de `"Ingrese su contraseña"` a `"Ingrese la contraseña"`.
- Se agregó `autocomplete="new-password"` al campo de contraseña.

### Todos los HTML de módulos
Afectados: `inicio`, `caja`, `compras`, `creditos`, `inventario`, `mantenimiento`, `reportes`, `seguridad` y `ventas`.

- Se **eliminó el bloque `<aside>` repetido** del sidebar en cada página.
- Se reemplazó por:
  ```html
  <div id="sidebar-contenedor"></div>
  ```
- Se agregaron los scripts compartidos:
  ```html
  <script src="../assets/inject-sidebar.js"></script>
  <script src="../assets/navegacion-persistente.js"></script>
  <script type="module" src="../assets/sesion.js"></script>
  <script type="module" src="../assets/verificar-sesion.js"></script>
  ```
- En `compras.html` y `ventas.html` además se agregó el script propio del módulo:
  - `<script type="module" src="compras.js"></script>`
  - `<script type="module" src="ventas.js"></script>`
- En `inicio.html` el título cambió de `Inicio - Mini Market` a `Dashboard - Mini Market`.
- En `seguridad.html` solo hay un cambio de espacio en blanco en `required` y la inclusión de los scripts compartidos.

---

## Archivos sin cambios reales

Los siguientes archivos aparecen como "modificados" por diferencias de saltos de línea (CRLF), pero su contenido es idéntico:

- `bodega.sql`
- `CAMBIOS_ESQUEMA_BD.md`
- `PLAN_BACKEND_SPRINGBOOT.md`
- `PLAN_ROLES_PERMISOS_BACKEND.md`
- `PLAN_SEGURIDAD_BACKEND.md`
- `vercel.json`
- `assets/theme.js`
- `assets/fonts.css`
- `assets/themes.css`
- `login/login.js`
- `login/loginForm.js`
- `mantenimiento/empleados.js`
- `seguridad/usuarios.js`
- `seguridad/roles.js`
- `seguridad/permisos.js`

---

## Impacto funcional de los cambios

- **Menú lateral unificado:** cualquier cambio futuro al menú se hace solo en `assets/sidebar.html`.
- **Navegación más fluida:** al cambiar de módulo ya no se recarga todo el sidebar; solo se reemplaza el contenido principal.
- **Persistencia de UI:** se recuerda qué grupos del acordeón están abiertos y la posición del scroll del sidebar.
- **Control de sesión:** las páginas protegidas redirigen al login si no hay sesión, y se cierra automáticamente tras 15 minutos de inactividad.
- **Módulos de Compras y Ventas:** antes los HTML existían, pero en `bodeguita` no tenían la lógica JS dedicada; ahora sí registran y listan compras/ventas.
- **Mejor experiencia en celulares:** el layout se adapta mejor a pantallas pequeñas.

---

## Observaciones técnicas

1. `inject-sidebar.js` usa `fetch('/assets/sidebar.html')` con ruta absoluta desde la raíz del sitio. Funciona en Vercel o en un servidor que sirva el proyecto desde `/`, pero **no** al abrir los HTML con `file://`.
2. `assets/verificar-sesion.js` hace una verificación de sesión similar a la que ahora también hace `inicio.js`, por lo que en la página de inicio se ejecutan ambas.
3. `navegacion-persistente.js` solo intercepta enlaces de `.html` dentro del sidebar; los enlaces externos y los que usan `data-bs-*` se dejan con el comportamiento normal.
4. La URL del backend de producción debe corresponder al servicio real en Render (`bodega-tia-matha-backend.onrender.com`).

(async function() {
  try {
    var contenedor = document.getElementById('sidebar-contenedor');
    if (!contenedor) {
      console.error('No se encontró #sidebar-contenedor');
      return;
    }

    var STORAGE_KEY = 'mmAccordionEstado';
    var SCROLL_KEY = 'mmSidebarScroll';
    var sidebarHTMLOriginal = null;

    function cargarSidebarHTML() {
      return fetch('/assets/sidebar.html', { cache: 'no-store' }).then(function(res) {
        if (!res.ok) throw new Error('No se pudo cargar sidebar.html');
        return res.text();
      });
    }

    function guardarEstado(clave, abierto) {
      var estado = {};
      try { estado = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}'); } catch(e) {}
      estado[clave] = abierto ? 'open' : 'closed';
      localStorage.setItem(STORAGE_KEY, JSON.stringify(estado));
    }

    function toggleIcon(btn) {
      var icon = btn.querySelector('.bi-chevron-down, .bi-chevron-up');
      if (!icon) return;
      if (btn.classList.contains('collapsed')) {
        icon.classList.remove('bi-chevron-down');
        icon.classList.add('bi-chevron-up');
      } else {
        icon.classList.remove('bi-chevron-up');
        icon.classList.add('bi-chevron-down');
      }
    }

    function initAcordeon() {
      document.querySelectorAll('#accordionProcesos .accordion-button, #menuMovil .accordion-button').forEach(function(btn) {
        btn.addEventListener('click', function() {
          var target = this.getAttribute('data-bs-target');
          if (!target) return;
          var clave = target.replace('#', '');
          var abierto = !this.classList.contains('collapsed');
          guardarEstado(clave, abierto);
          toggleIcon(this);
        });
      });
    }

    function restaurarAcordeon() {
      var estado = {};
      try { estado = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}'); } catch(e) {}
      document.querySelectorAll('#accordionProcesos .accordion-button, #menuMovil .accordion-button').forEach(function(btn) {
        var target = btn.getAttribute('data-bs-target');
        if (!target) return;
        var clave = target.replace('#', '');
        var colapsable = document.querySelector(target);
        if (!colapsable) return;
        var icon = btn.querySelector('.bi-chevron-down, .bi-chevron-up');

        if (estado[clave] === 'open') {
          btn.classList.remove('collapsed');
          btn.setAttribute('aria-expanded', 'true');
          colapsable.classList.remove('collapse');
          colapsable.classList.add('show');
          if (icon) {
            icon.classList.remove('bi-chevron-down');
            icon.classList.add('bi-chevron-up');
          }
        } else {
          btn.classList.add('collapsed');
          btn.setAttribute('aria-expanded', 'false');
          colapsable.classList.remove('show');
          colapsable.classList.add('collapse');
          if (icon) {
            icon.classList.remove('bi-chevron-up');
            icon.classList.add('bi-chevron-down');
          }
        }
      });
    }

    // Copia la misma estructura del sidebar dentro del menú móvil (offcanvas),
    // renombrando los IDs para evitar duplicados con el sidebar de escritorio.
    function inyectarMenuMovil(htmlSidebar) {
      var body = document.querySelector('#menuMovil .offcanvas-body');
      if (!body) return;

      var temp = document.createElement('div');
      temp.innerHTML = htmlSidebar;
      var aside = temp.querySelector('aside');
      if (!aside) return;

      var contenido = document.createElement('div');
      contenido.className = 'menu-movil-contenido d-flex flex-column h-100';

      // Se omiten el logo y el <hr> inicial porque el offcanvas ya tiene su encabezado
      Array.prototype.slice.call(aside.children, 2).forEach(function(nodo) {
        contenido.appendChild(nodo.cloneNode(true));
      });

      var ids = {};
      contenido.querySelectorAll('[id]').forEach(function(el) {
        ids[el.id] = true;
        el.id = 'movil-' + el.id;
      });

      contenido.querySelectorAll('[data-bs-target^="#"]').forEach(function(el) {
        var target = el.getAttribute('data-bs-target').slice(1);
        if (ids[target]) el.setAttribute('data-bs-target', '#movil-' + target);
      });

      contenido.querySelectorAll('[aria-controls]').forEach(function(el) {
        var control = el.getAttribute('aria-controls');
        if (ids[control]) el.setAttribute('aria-controls', 'movil-' + control);
      });

      contenido.querySelectorAll('a[href^="#"]').forEach(function(el) {
        var href = el.getAttribute('href').slice(1);
        if (ids[href]) el.setAttribute('href', '#movil-' + href);
      });

      body.innerHTML = '';
      body.appendChild(contenido);
    }

    // Activa la pestaña indicada por el hash (ej. #pane-usuarios)
    function activarTabDesdeHash(hash) {
      if (!hash) return;
      var pane = document.querySelector(hash);
      if (!pane || !pane.classList.contains('tab-pane')) return;
      var tab = document.querySelector(
        '[data-bs-toggle="tab"][data-bs-target="' + hash + '"]'
      );
      if (!tab) return;
      if (window.bootstrap && window.bootstrap.Tab) {
        window.bootstrap.Tab.getOrCreateInstance(tab).show();
      } else {
        tab.click();
      }
    }

    // Marca el enlace exacto (ruta + hash) y abre su grupo de acordeón
    function marcarEnlaceActivo() {
      var rutaActual = window.location.pathname;
      var hashActual = window.location.hash;
      var raices = [contenedor];
      var menuMovil = document.querySelector('#menuMovil .offcanvas-body');
      if (menuMovil) raices.push(menuMovil);

      raices.forEach(function(raiz) {
        raiz.querySelectorAll('.nav-link').forEach(function(link) {
          var href = link.getAttribute('href') || '';
          var partes = href.split('#');
          var rutaEnlace = partes[0];
          var hashEnlace = partes[1] ? '#' + partes[1] : '';
          var archivoEnlace = rutaEnlace.split('/').pop();

          var mismaRuta =
            rutaEnlace === rutaActual ||
            (archivoEnlace !== '' && rutaActual.endsWith('/' + archivoEnlace));
          var mismoHash = hashActual ? hashEnlace === hashActual : !hashEnlace;
          var activo = mismaRuta && mismoHash;

          link.classList.toggle('active', activo);
          if (activo) {
            link.setAttribute('aria-current', 'page');
            abrirGrupoDe(link);
          } else {
            link.removeAttribute('aria-current');
          }
        });
      });
    }

    function abrirGrupoDe(link) {
      var grupo = link.closest('.accordion-collapse');
      if (!grupo || grupo.classList.contains('show')) return;
      var boton = document.querySelector(
        '[data-bs-toggle="collapse"][data-bs-target="#' + grupo.id + '"]'
      );
      if (!boton) return;
      if (window.bootstrap && window.bootstrap.Collapse) {
        window.bootstrap.Collapse.getOrCreateInstance(grupo, { toggle: false }).show();
      } else {
        boton.click();
      }
    }

    function restaurarScroll(valor) {
      var aside = document.querySelector('aside');
      if (aside && valor !== null && valor !== undefined) {
        aside.scrollTop = parseInt(valor, 10);
      }
    }

    function renderizarSidebar(html) {
      contenedor.innerHTML = html;
      sidebarHTMLOriginal = html;
      inyectarMenuMovil(html);
      initAcordeon();
      restaurarAcordeon();
      marcarEnlaceActivo();
      activarTabDesdeHash(window.location.hash);
    }

    // Vuelve a leer sidebar.html y solo reemplaza el menú si su contenido cambió.
    // Así los cambios se ven al navegar entre vistas sin recargar la página.
    function actualizarSidebar() {
      return cargarSidebarHTML()
        .then(function(html) {
          if (html === sidebarHTMLOriginal) return false;

          var asideActual = document.querySelector('aside');
          var scroll = asideActual ? asideActual.scrollTop : null;

          renderizarSidebar(html);
          restaurarScroll(scroll);
          return true;
        })
        .catch(function(e) {
          console.error('Error actualizando sidebar:', e);
          return false;
        });
    }

    // Expone utilidades para que navegacion-persistente las use tras cada navegación
    window.navegacionSidebar = {
      marcarEnlaceActivo: marcarEnlaceActivo,
      activarTabDesdeHash: activarTabDesdeHash,
      actualizarSidebar: actualizarSidebar
    };

    var htmlInicial = await cargarSidebarHTML();
    renderizarSidebar(htmlInicial);

    // Restaurar scroll guardado
    var scrollGuardado = sessionStorage.getItem(SCROLL_KEY);
    if (scrollGuardado !== null) {
      restaurarScroll(scrollGuardado);
      sessionStorage.removeItem(SCROLL_KEY);
    }

    // Guardar scroll al cambiar de página (recarga completa)
    window.addEventListener('beforeunload', function() {
      var a = document.querySelector('aside');
      if (a) sessionStorage.setItem(SCROLL_KEY, a.scrollTop);
    });

  } catch (e) {
    console.error('Error inyectando sidebar:', e);
  }
})();

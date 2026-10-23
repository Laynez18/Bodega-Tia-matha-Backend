(function () {
  // Se añade inject-sidebar para que no se re-ejecute en cada navegación SPA
  var GLOBAL_SRC = /bootstrap|theme\.js|navegacion-persistente|inject-sidebar/;
  var EVENTO_CAMBIO = 'navegacion-persistente-cambio';

  // Registro de funciones que recargan los datos de un panel (pestaña).
  // Cada módulo registra su función con window.registrarRecargaPanel.
  var panelesRecargables = {};

  window.registrarRecargaPanel = function (paneId, fn) {
    if (!paneId || typeof fn !== 'function') return;
    panelesRecargables[String(paneId).replace(/^#/, '')] = fn;
  };

  function extraerDeHTML(htmlTexto) {
    var parser = new DOMParser();
    var doc = parser.parseFromString(htmlTexto, 'text/html');
    var body = doc.body;
    return {
      title: doc.title,
      main: body.querySelector('main'),
      modales: Array.from(body.querySelectorAll('.modal')),
      scripts: Array.from(body.querySelectorAll('script'))
    };
  }

  function ejecutarScripts(scripts, baseUrl) {
    scripts.forEach(function (original) {
      if (original.src && GLOBAL_SRC.test(original.src)) return;
      var s = document.createElement('script');
      Array.from(original.attributes).forEach(function (attr) {
        s.setAttribute(attr.name, attr.value);
      });
      if (original.src) {
        var src = original.getAttribute('src');
        // Resolver contra la página destino (no contra la actual) y forzar
        // una URL única para que los módulos se vuelvan a evaluar.
        var absoluta = src;
        try {
          absoluta = new URL(src, new URL(baseUrl, window.location.href)).href;
        } catch (e) {}
        s.src = absoluta + (absoluta.indexOf('?') === -1 ? '?' : '&') + 'spa=' + Date.now();
      } else {
        s.text = original.textContent;
      }
      s.setAttribute('data-persistente', 'true');
      document.head.appendChild(s);
      if (!original.src) {
        s.onload = function () { s.remove(); };
        s.onerror = function () { s.remove(); };
      }
    });
  }

  function actualizarActivo() {
    if (window.navegacionSidebar) {
      window.navegacionSidebar.marcarEnlaceActivo();
      return;
    }

    var ruta = window.location.pathname.split('/').pop();
    document.querySelectorAll('aside .nav-link').forEach(function (link) {
      var href = link.getAttribute('href');
      if (!href) return;
      var rutaEnlace = href.split('#')[0];
      var coincide = rutaEnlace.endsWith('/' + ruta) || rutaEnlace === ruta;
      link.classList.toggle('active', coincide);
      link.setAttribute('aria-current', coincide ? 'page' : null);
    });
  }

  function activarHash(hash) {
    if (window.navegacionSidebar) {
      window.navegacionSidebar.activarTabDesdeHash(hash);
    }
  }

  function navegar(url, desdePopstate) {
    var partes = url.split('#');
    var ruta = partes[0];
    var hash = partes[1] ? '#' + partes[1] : '';

    if (!/\.html$/i.test(ruta)) return;

    var urlActual = window.location.pathname + window.location.search + window.location.hash;
    if (!desdePopstate && ruta + hash === urlActual) return;

    // Si solo cambia la pestaña de la página actual, no hace falta recargar el main
    if (ruta === window.location.pathname && !window.location.search && hash) {
      if (!desdePopstate) history.pushState(null, '', ruta + hash);
      if (window.navegacionSidebar && window.navegacionSidebar.actualizarSidebar) {
        window.navegacionSidebar.actualizarSidebar();
      }
      activarHash(hash);
      actualizarActivo();
      window.dispatchEvent(new CustomEvent(EVENTO_CAMBIO, { detail: { url: ruta + hash } }));
      return;
    }

    // Guardar scroll del sidebar antes de navegar
    var asideActual = document.querySelector('aside');
    if (asideActual) {
      sessionStorage.setItem('mmSidebarScroll', asideActual.scrollTop);
    }

    fetch(ruta, { headers: { 'X-Requested-With': 'fetch' } })
      .then(function (res) {
        if (!res.ok) throw new Error('No se pudo cargar ' + url);
        return res.text();
      })
      .then(function (html) {
        var extraido = extraerDeHTML(html);

        // Solo reemplazar el <main>; el sidebar se refresca aparte si cambió.
        var mainActual = document.querySelector('main');
        if (mainActual) {
          mainActual.remove();
        }

        var contenedorPagina = document.querySelector('.d-flex') || document.body;
        if (extraido.main) {
          var aside = contenedorPagina.querySelector('aside');
          if (aside && aside.parentNode === contenedorPagina) {
            aside.parentNode.insertBefore(extraido.main, aside.nextSibling);
          } else {
            contenedorPagina.appendChild(extraido.main);
          }
        }

        // Reemplazar modales
        document.body.querySelectorAll('.modal').forEach(function (m) { m.remove(); });
        extraido.modales.forEach(function (m) { document.body.appendChild(m); });

        // Refrescar el sidebar si assets/sidebar.html cambió
        if (window.navegacionSidebar && window.navegacionSidebar.actualizarSidebar) {
          window.navegacionSidebar.actualizarSidebar();
        }

        // Ejecutar scripts de la página destino (menos los globales)
        document.querySelectorAll('script[data-persistente="true"]').forEach(function (s) { s.remove(); });
        ejecutarScripts(extraido.scripts, ruta);

        // Restaurar scroll del sidebar
        var scrollGuardado = sessionStorage.getItem('mmSidebarScroll');
        if (scrollGuardado !== null) {
          var aside2 = document.querySelector('aside');
          if (aside2) {
            aside2.scrollTop = parseInt(scrollGuardado, 10);
          }
          sessionStorage.removeItem('mmSidebarScroll');
        }

        if (extraido.title) document.title = extraido.title;
        if (desdePopstate) {
          history.replaceState(null, '', ruta + hash);
        } else {
          history.pushState(null, '', ruta + hash);
        }
        actualizarActivo();
        activarHash(hash);

        window.dispatchEvent(new CustomEvent(EVENTO_CAMBIO, { detail: { url: ruta + hash } }));
      })
      .catch(function (err) {
        console.error(err);
        window.location.href = ruta + hash;
      });
  }

  function inicializar() {
    actualizarActivo();

    // Al mostrar una pestaña, recargar sus datos desde la API.
    document.addEventListener('shown.bs.tab', function (evento) {
      var objetivo =
        evento.target.getAttribute('data-bs-target') ||
        evento.target.getAttribute('href') ||
        '';
      if (objetivo.charAt(0) !== '#') return;
      var recargar = panelesRecargables[objetivo.slice(1)];
      if (recargar) recargar();
    });

    document.body.addEventListener('click', function (e) {
      var enlace = e.target.closest('aside a[href], #menuMovil a[href]');
      if (!enlace) return;
      var href = enlace.getAttribute('href');
      if (!href || href.startsWith('#') || href.startsWith('http')) return;
      if (enlace.getAttribute('data-bs-toggle') || enlace.getAttribute('data-bs-target')) return;
      if (enlace.getAttribute('data-bs-dismiss')) return;
      e.preventDefault();

      // Cierra el menú móvil antes de navegar
      var offcanvas = enlace.closest('.offcanvas');
      if (offcanvas && window.bootstrap && window.bootstrap.Offcanvas) {
        window.bootstrap.Offcanvas.getOrCreateInstance(offcanvas).hide();
      }

      navegar(href);
    });

    window.addEventListener('popstate', function () {
      navegar(window.location.pathname + window.location.search + window.location.hash, true);
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializar);
  } else {
    inicializar();
  }

  window.navegacionPersistente = { navegarA: navegar, EVENTO_CAMBIO: EVENTO_CAMBIO };
})();

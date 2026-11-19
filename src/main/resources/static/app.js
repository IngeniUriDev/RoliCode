// Función para cambiar entre la pestaña de Portafolio y la de Demos
function mostrarPestana(nombrePestana, boton) {
    document.getElementById('vista-inicio').classList.toggle('hidden', nombrePestana !== 'inicio');
    document.getElementById('vista-demos').classList.toggle('hidden', nombrePestana !== 'demos');
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    boton.classList.add('active');

    // Al entrar a la pestaña de Demos, mantenemos los demos cerrados hasta que el usuario haga clic en uno
    if (nombrePestana === 'demos') {
        cerrarDemoInventario();
    }
}

// Al abrir la página, el Navegador carga los datos del Portafolio y del Sistema de Inventario
async function cargarDatosDesdeSpringBoot() {
    // 1. Pedir Perfil (incluyendo biografia y tecnologias agregadas por Uriel) -> GET /api/portafolio/perfil
    const resPerfil = await fetch('/api/portafolio/perfil');
    const perfil = await resPerfil.json();
    document.getElementById('nav-empresa').textContent = perfil.empresa;
    const navEstado = document.getElementById('nav-estado');
    if (navEstado) navEstado.textContent = perfil.estado;
    document.getElementById('hero-nombre').textContent = 'Hola, somos ' + perfil.empresa;
    document.getElementById('hero-rol').textContent = perfil.rol;

    if (perfil.biografia) {
        document.getElementById('hero-bio').textContent = perfil.biografia;
    }
    if (perfil.tecnologias) {
        document.getElementById('hero-tech').innerHTML = perfil.tecnologias
            .split(',')
            .map(t => `<span class="status-tag status-ready" style="margin: 0.2rem;">${t.trim()}</span>`)
            .join('');
    }

    // 2. Pedir Servicios -> GET /api/portafolio/servicios
    // La descripción empieza oculta (class="hidden") y se despliega al hacer clic en el contenedor
    const resServicios = await fetch('/api/portafolio/servicios');
    const servicios = await resServicios.json();
    document.getElementById('contenedor-servicios').innerHTML = servicios.map(s => `
        <div class="card" style="cursor: pointer;" onclick="alternarDetalleServicio(${s.id})">
            <span class="card-code">#${s.codigo}</span>
            <h3>${s.titulo} <span style="font-size:0.85rem; color:var(--accent);">▼</span></h3>
            <div id="detalle-servicio-${s.id}" class="hidden" style="margin-top: 0.6rem; border-top: 1px solid #334155; padding-top: 0.6rem;">
                <p>${s.descripcion}</p>
            </div>
        </div>
    `).join('');

    // 3. Pedir Demos -> GET /api/portafolio/demos
    // Al hacer clic en el contenedor de un Demo activo, se despliega ese Demo abajo
    const resDemos = await fetch('/api/portafolio/demos');
    const demos = await resDemos.json();
    document.getElementById('contenedor-demos').innerHTML = demos.map(d => `
        <div class="card" style="cursor: pointer;" onclick="seleccionarDemo(${d.id}, ${d.activo})">
            <h3>${d.nombre}</h3>
            <p>${d.descripcion}</p>
            <span class="status-tag ${d.activo ? 'status-ready' : 'status-wip'}">
                ${d.activo ? 'Haz clic para abrir el Demo' : 'Próximamente'}
            </span>
        </div>
    `).join('');

    // 4. Cargar Solicitudes de Contacto y preparar los datos del Inventario
    await cargarSolicitudesGuardadas();
    await cargarInventarioYVentas();
}

// Despliega u oculta la información de un Servicio al hacer clic en su tarjeta
function alternarDetalleServicio(idServicio) {
    const detalle = document.getElementById('detalle-servicio-' + idServicio);
    if (detalle) {
        detalle.classList.toggle('hidden');
    }
}

// Despliega el Demo seleccionado cuando el usuario hace clic en su contenedor
function seleccionarDemo(idDemo, activo) {
    if (!activo) {
        alert('Este proyecto demo está en construcción. Pronto estará disponible.');
        return;
    }
    if (idDemo === 1) {
        const panel = document.getElementById('panel-demo-inventario');
        if (panel) {
            panel.classList.toggle('hidden');
            if (!panel.classList.contains('hidden')) {
                panel.scrollIntoView({ behavior: 'smooth' });
            }
        }
    }
}

function cerrarDemoInventario() {
    const panel = document.getElementById('panel-demo-inventario');
    if (panel) {
        panel.classList.add('hidden');
    }
}

// Consulta las solicitudes en la Base de Datos
async function cargarSolicitudesGuardadas() {
    const res = await fetch('/api/portafolio/contacto');
    const solicitudes = await res.json();
    const contenedor = document.getElementById('lista-solicitudes');

    if (solicitudes.length === 0) {
        contenedor.innerHTML = '<p style="color: var(--muted);">Aún no hay solicitudes registradas en la Base de Datos.</p>';
        return;
    }

    contenedor.innerHTML = solicitudes.map(sol => `
        <div class="card">
            <span class="card-code">Ticket SQL #${sol.id} • ${sol.servicioInteres}</span>
            <h3>${sol.nombreCliente} (${sol.email})</h3>
            <p>${sol.mensaje}</p>
            <span class="status-tag status-ready">Presupuesto: $${sol.presupuesto} USD</span>
        </div>
    `).join('');
}

// Envía una nueva solicitud de contacto mediante POST /api/portafolio/contacto
async function enviarFormularioContacto(evento) {
    evento.preventDefault();
    const nuevaSolicitud = {
        nombreCliente: document.getElementById('input-nombre').value,
        email: document.getElementById('input-email').value,
        servicioInteres: document.getElementById('input-servicio').value,
        presupuesto: parseFloat(document.getElementById('input-presupuesto').value),
        mensaje: document.getElementById('input-mensaje').value
    };

    const respuesta = await fetch('/api/portafolio/contacto', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevaSolicitud)
    });

    if (respuesta.ok) {
        document.getElementById('form-contacto').reset();
        await cargarSolicitudesGuardadas();
        alert('¡Solicitud guardada con éxito en la Base de Datos SQL!');
    } else {
        alert('Regla del Chef (Service): El presupuesto mínimo aceptado es de $100 USD.');
    }
}

// =========================================================================
// LÓGICA DEL DEMO #1: SISTEMA DE INVENTARIO, ENTRADAS, SALIDAS Y VENTAS
// =========================================================================

async function cargarInventarioYVentas() {
    const resProd = await fetch('/api/inventario/productos');
    const productos = await resProd.json();

    const resVentas = await fetch('/api/inventario/ventas');
    const ventas = await resVentas.json();

    const totalUnidades = productos.reduce((acc, p) => acc + p.stockActual, 0);
    const totalIngresos = ventas.reduce((acc, v) => acc + v.totalVenta, 0);
    const totalGanancia = ventas.reduce((acc, v) => acc + v.gananciaNeta, 0);

    document.getElementById('kpi-productos').textContent = productos.length;
    document.getElementById('kpi-unidades').textContent = totalUnidades;
    document.getElementById('kpi-ingresos').textContent = '$' + totalIngresos.toFixed(2);
    document.getElementById('kpi-ganancia').textContent = '$' + totalGanancia.toFixed(2);

    pintarTablaProductos(productos);

    const tbodyVentas = document.getElementById('tabla-ventas');
    if (ventas.length === 0) {
        tbodyVentas.innerHTML = '<tr><td colspan="5" style="color: var(--muted); text-align:center;">Aún no se han registrado ventas. Usa el botón "Vender" arriba.</td></tr>';
    } else {
        tbodyVentas.innerHTML = ventas.map(v => `
            <tr>
                <td>#${v.id}</td>
                <td>${v.nombreProducto}</td>
                <td>${v.cantidad} uds</td>
                <td style="color: var(--accent-green); font-weight:600;">$${v.totalVenta.toFixed(2)}</td>
                <td style="color: var(--accent-amber); font-weight:600;">+$${v.gananciaNeta.toFixed(2)}</td>
            </tr>
        `).join('');
    }
}

function pintarTablaProductos(productos) {
    document.getElementById('tabla-productos').innerHTML = productos.map(p => `
        <tr>
            <td><strong>${p.codigo}</strong></td>
            <td>${p.nombre}</td>
            <td>$${p.precioCompra.toFixed(2)}</td>
            <td><strong>$${p.precioVenta.toFixed(2)}</strong></td>
            <td>
                <span class="status-tag ${p.stockActual <= 5 ? 'status-low' : 'status-ready'}">
                    ${p.stockActual} uds ${p.stockActual <= 5 ? '(Bajo)' : ''}
                </span>
            </td>
            <td>
                <div class="action-group">
                    <input type="number" id="qty-${p.id}" class="qty-input" value="1" min="1">
                    <button class="btn-sm btn-in" onclick="ejecutarMovimiento(${p.id}, 'entrada')">+ Entrada</button>
                    <button class="btn-sm btn-out" onclick="ejecutarMovimiento(${p.id}, 'salida')">- Salida</button>
                    <button class="btn-sm btn-sell" onclick="ejecutarMovimiento(${p.id}, 'venta')">Vender</button>
                    <button class="btn-sm btn-del" onclick="eliminarProducto(${p.id})" title="Eliminar producto">Eliminar</button>
                </div>
            </td>
        </tr>
    `).join('');
}

async function buscarProductosEnVivo() {
    const texto = document.getElementById('input-buscar').value;
    const res = await fetch(`/api/inventario/buscar?texto=${encodeURIComponent(texto)}`);
    const productosFiltrados = await res.json();
    pintarTablaProductos(productosFiltrados);
}

async function filtrarPorStockBajo() {
    const res = await fetch('/api/inventario/stock-bajo?limite=5');
    const productosBajos = await res.json();
    pintarTablaProductos(productosBajos);
}

async function ejecutarMovimiento(idProducto, tipo) {
    const cantidad = parseInt(document.getElementById('qty-' + idProducto).value);
    const res = await fetch(`/api/inventario/${tipo}?idProducto=${idProducto}&cantidad=${cantidad}`, {
        method: 'POST'
    });

    if (res.ok) {
        await cargarInventarioYVentas();
    } else {
        alert('Regla de Negocio en InventarioService: Stock insuficiente en bodega o cantidad inválida.');
    }
}

async function crearNuevoProducto(evento) {
    evento.preventDefault();
    const nuevo = {
        codigo: document.getElementById('prod-codigo').value,
        nombre: document.getElementById('prod-nombre').value,
        precioCompra: parseFloat(document.getElementById('prod-compra').value),
        precioVenta: parseFloat(document.getElementById('prod-venta').value),
        stockActual: parseInt(document.getElementById('prod-stock').value)
    };

    const res = await fetch('/api/inventario/productos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevo)
    });

    if (res.ok) {
        document.getElementById('form-producto').reset();
        await cargarInventarioYVentas();
    } else {
        alert('Regla del Chef (InventarioService): El Precio de Venta no puede ser menor al Costo de Compra, o se alcanzó el límite máximo de 10 productos del Demo.');
    }
}

async function eliminarProducto(idProducto) {
    if (!confirm('¿Seguro que deseas eliminar este producto del inventario?')) return;
    const res = await fetch(`/api/inventario/productos?idProducto=${idProducto}`, {
        method: 'DELETE'
    });
    if (res.ok) {
        await cargarInventarioYVentas();
    } else {
        alert('Protección de Memoria del Demo: Deben quedar al menos 3 productos de muestra en el inventario.');
    }
}

// =========================================================================
// LÓGICA DE LA ESCENA LUNAR Y OLAS ANIMADAS CON EL SCROLL (Lección 15)
// =========================================================================

// Configuración de las 4 olas (igual que en el CodePen: altura base, amplitud y velocidad)
const configOlas = [
    { id: 'ola-1', base: 34, swing: 4.5, speed: 1.0, step: 0.24 },
    { id: 'ola-2', base: 27, swing: 5.2, speed: -1.2, step: 0.31 },
    { id: 'ola-3', base: 20, swing: 6.0, speed: 1.8, step: 0.38 },
    { id: 'ola-4', base: 13, swing: 6.2, speed: -2.2, step: 0.45 }
];

function inicializarOlasLunares() {
    // En vez de escribir 160 etiquetas <i> a mano en el HTML, las creamos con un ciclo:
    configOlas.forEach(ola => {
        const contenedor = document.getElementById(ola.id);
        if (!contenedor) return;
        let barrasHTML = '';
        for (let i = 0; i < 40; i++) {
            barrasHTML += '<i></i>';
        }
        contenedor.innerHTML = barrasHTML;
    });

    // Pintamos la posición inicial de las olas y escuchamos el evento "scroll" del mouse
    actualizarOlasConScroll();
    window.addEventListener('scroll', actualizarOlasConScroll);
}

function actualizarOlasConScroll() {
    const scrollY = window.scrollY || 0;
    // Convertimos los píxeles de scroll en un ángulo (radianes) para la función matemática Seno (Math.sin)
    const anguloScroll = scrollY * 0.015;

    // Efecto Parallax suave en la Luna al bajar con el scroll
    const luna = document.querySelector('.luna-nocturna');
    if (luna) {
        luna.style.transform = `translate(-50%, ${scrollY * 0.12}px)`;
    }

    // Calculamos la altura de cada barrita <i> usando la onda Seno (Math.sin) igual que en el CodePen
    configOlas.forEach(ola => {
        const contenedor = document.getElementById(ola.id);
        if (!contenedor) return;
        const barras = contenedor.querySelectorAll('i');
        barras.forEach((barra, index) => {
            const onda = Math.sin(anguloScroll * ola.speed + index * ola.step);
            const alturaVh = ola.base + onda * ola.swing;
            barra.style.height = `${alturaVh}vh`;
        });
    });
}

// =========================================================================
// CUMPLIMIENTO LEGAL: BANNER DE COOKIES, PRIVACIDAD, TÉRMINOS Y BAJA (ARCO)
// =========================================================================

function inicializarBannerCookies() {
    const decision = localStorage.getItem('rolicode_cookies_consent');
    const banner = document.getElementById('banner-cookies');
    if (banner && !decision) {
        banner.classList.remove('hidden');
    } else if (banner) {
        banner.style.display = 'none';
    }
}

function aceptarCookies(tipo) {
    localStorage.setItem('rolicode_cookies_consent', tipo);
    const banner = document.getElementById('banner-cookies');
    if (banner) {
        banner.style.display = 'none';
    }
}

function abrirModalLegal(seccion) {
    const modal = document.getElementById('modal-legal');
    const titulo = document.getElementById('titulo-modal-legal');
    const contenido = document.getElementById('contenido-modal-legal');
    if (!modal) return;

    modal.classList.remove('hidden');

    if (seccion === 'privacidad') {
        titulo.textContent = 'Política de Privacidad y Protección de Datos (RoliCode)';
        contenido.innerHTML = `
            <p><strong>1. Responsable de los Datos:</strong> Uriel (RoliCode Desarrollo Full-Stack).</p>
            <p style="margin-top:0.5rem;"><strong>2. Datos Recabados:</strong> Nombre/Empresa, correo electrónico, servicio de interés, presupuesto estimado y detalles del proyecto enviados voluntariamente mediante el formulario de cotización.</p>
            <p style="margin-top:0.5rem;"><strong>3. Finalidad:</strong> Exclusivamente evaluar, cotizar y dar seguimiento a los servicios de desarrollo solicitados. RoliCode no vende ni transfiere tus datos personales a terceros.</p>
            <p style="margin-top:0.5rem;"><strong>4. Derechos ARCO / Baja de Correos:</strong> Puedes ejercer tu derecho de cancelación y baja inmediata en cualquier momento desde la opción <em>"Baja de Correos"</em> al pie de esta página.</p>
        `;
    } else if (seccion === 'terminos') {
        titulo.textContent = 'Términos y Condiciones de Uso y Servicios';
        contenido.innerHTML = `
            <p><strong>1. Uso del Portafolio y Laboratorio de Demos:</strong> Los proyectos en la pestaña "Laboratorio de Demos" (como el Sistema de Inventario y POS) son entornos demostrativos interactivos propiedad de RoliCode.</p>
            <p style="margin-top:0.5rem;"><strong>2. Cotizaciones y Presupuestos:</strong> El envío de una solicitud de cotización no constituye un contrato automático; toda prestación de servicios de desarrollo se formaliza mediante propuesta técnica y acuerdo firmado entre el cliente y RoliCode (presupuesto mínimo de referencia: $100 USD).</p>
            <p style="margin-top:0.5rem;"><strong>3. Propiedad Intelectual:</strong> El código fuente, arquitectura y logotipo de RoliCode están protegidos por las leyes aplicables de propiedad intelectual.</p>
        `;
    } else if (seccion === 'licencias') {
        titulo.textContent = 'Auditoría de Licencias (Fuentes, Imágenes, Logos y Software)';
        contenido.innerHTML = `
            <ul style="padding-left: 1.2rem;">
                <li><strong>Logotipo RoliCode:</strong> Diseño original de Uriel implementado en código vectorial SVG puro (100% autoría propia, libre de regalías de terceros).</li>
                <li><strong>Animación Lunar y Olas:</strong> Generadas matemáticamente mediante código CSS3 y JavaScript (<code>Math.sin</code>) sin utilizar imágenes con copyright.</li>
                <li><strong>Tipografías (Fuentes):</strong> Uso de la pila nativa del sistema operativo (<code>Segoe UI, system-ui, sans-serif</code>) conforme al estándar abierto W3C, sin redistribuir fuentes comerciales restringidas.</li>
                <li><strong>Motor Backend:</strong> OpenJDK 21, Spring Boot 4 (Apache License 2.0) y PostgreSQL JDBC (PostgreSQL License).</li>
            </ul>
        `;
    } else if (seccion === 'baja') {
        titulo.textContent = 'Baja de Correos y Eliminación de Datos (Derecho al Olvido)';
        contenido.innerHTML = `
            <p style="margin-bottom: 0.75rem;">Si enviaste una solicitud de cotización y deseas dar de baja tu correo electrónico y eliminar tus registros de nuestra base de datos SQL, ingresa tu correo a continuación:</p>
            <form onsubmit="ejecutarBajaDeCorreo(event)" style="display: flex; gap: 0.6rem; flex-wrap: wrap;">
                <input type="email" id="input-email-baja" placeholder="Ingresa tu correo registrado (ej. ana@empresa.com)" style="max-width: 340px;" required>
                <button type="submit" class="btn-sm btn-del" style="padding: 0.6rem 1.1rem;">Dar de Baja mi Correo</button>
            </form>
        `;
    }

    modal.scrollIntoView({ behavior: 'smooth' });
}

function cerrarModalLegal() {
    const modal = document.getElementById('modal-legal');
    if (modal) modal.classList.add('hidden');
}

async function ejecutarBajaDeCorreo(evento) {
    evento.preventDefault();
    const email = document.getElementById('input-email-baja').value;
    const res = await fetch(`/api/portafolio/contacto/baja?email=${encodeURIComponent(email)}`, {
        method: 'DELETE'
    });
    if (res.ok) {
        const data = await res.json();
        alert(`${data.mensaje} (Registros eliminados: ${data.registrosEliminados})`);
        cerrarModalLegal();
        await cargarSolicitudesGuardadas();
    } else {
        alert('No se encontró ningún registro activo con ese correo electrónico en la base de datos.');
    }
}

inicializarBannerCookies();
inicializarOlasLunares();
cargarDatosDesdeSpringBoot();

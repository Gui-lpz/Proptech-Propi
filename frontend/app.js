const BASE = "http://localhost:8080/api";

const token = localStorage.getItem("jwt_token");
const roles = JSON.parse(localStorage.getItem("roles") || "[]");

if (!token) {
    location.href = "./login.html";
}

const mensaje = document.getElementById("mensaje");
document.getElementById("usuarioActual").textContent =
    localStorage.getItem("username") || "";


/* MENÚ HAMBURGUESA */

const menuToggle =
    document.getElementById("menuToggle");

const menuOverlay =
    document.getElementById("menuOverlay");

function alternarMenu() {
    if (window.innerWidth <= 850) {
        document.body.classList.toggle("menu-movil-abierto");

        const abierto =
            document.body.classList.contains(
                "menu-movil-abierto"
            );

        menuToggle?.setAttribute(
            "aria-expanded",
            abierto ? "true" : "false"
        );

        return;
    }

    document.body.classList.toggle("menu-colapsado");

    const expandido =
        !document.body.classList.contains(
            "menu-colapsado"
        );

    menuToggle?.setAttribute(
        "aria-expanded",
        expandido ? "true" : "false"
    );
}

menuToggle?.addEventListener(
    "click",
    alternarMenu
);

menuOverlay?.addEventListener(
    "click",
    () => {
        document.body.classList.remove(
            "menu-movil-abierto"
        );

        menuToggle?.setAttribute(
            "aria-expanded",
            "false"
        );
    }
);

window.addEventListener(
    "resize",
    () => {
        if (window.innerWidth > 850) {
            document.body.classList.remove(
                "menu-movil-abierto"
            );
        }
    }
);


/*ROLES*/

function tieneRol(rol) {
    return roles.includes(rol);
}

const esAdmin = tieneRol("ROLE_ADMINISTRADOR");
const esDigitador = tieneRol("ROLE_DIGITADOR");
const esConsulta = tieneRol("ROLE_CONSULTA");

function configurarRol() {

    const clienteForm =
        document.getElementById("clienteForm");

    const cabysForm =
        document.getElementById("cabysForm");

    const productoForm =
        document.getElementById("productoForm");

    const impuestoForm =
        document.getElementById("impuestoForm");

    const facturaForm =
        document.getElementById("facturaForm");

    const notaForm =
        document.getElementById("notaForm");


    if (clienteForm) {
        clienteForm.style.display =
            (esAdmin || esDigitador)
                ? ""
                : "none";
    }


    if (cabysForm) {
        cabysForm.style.display =
            esAdmin
                ? ""
                : "none";
    }


    if (productoForm) {
        productoForm.style.display =
            esAdmin
                ? ""
                : "none";
    }


    if (impuestoForm) {
        impuestoForm.style.display =
            esAdmin
                ? ""
                : "none";
    }


    if (facturaForm) {
        facturaForm.style.display =
            (esAdmin || esDigitador)
                ? ""
                : "none";
    }


    if (notaForm) {
        notaForm.style.display =
            (esAdmin || esDigitador)
                ? ""
                : "none";
    }



    document
        .querySelectorAll(".reporte-control")
        .forEach(
            elemento => {
                elemento.style.display =
                    (esAdmin || esConsulta)
                        ? ""
                        : "none";
            }
        );
}


/* API*/

async function api(path, options = {}) {
    const response = await fetch(`${BASE}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            ...(options.headers || {})
        }
    });

    if (response.status === 401) {

        localStorage.removeItem("jwt_token");
        localStorage.removeItem("username");
        localStorage.removeItem("roles");
        localStorage.removeItem("sessionId");

        location.href = "./login.html";
        return null;
    }


    if (response.status === 403) {
        throw new Error("No tiene permisos para realizar esta operación.");
    }

    if (response.status === 204) {
        return null;
    }

    let data = null;

    try {
        data = await response.json();
    } catch {
        data = null;
    }

    if (!response.ok) {
        throw new Error(
            data?.message
            || data?.error
            || "No fue posible completar la operación."
        );
    }

    return data;
}

/*
 * REQ 003: Cerrar sesión.
 *
 * Registra la salida en SQL Server antes
 * de eliminar los datos del navegador.
 */



async function cerrarSesion() {

    const tokenActual =
        localStorage.getItem("jwt_token");

    const sessionId =
        localStorage.getItem("sessionId");

    try {

        if (tokenActual && sessionId) {

            const response = await fetch(
                `${BASE}/auth/logout`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${tokenActual}`
                    },
                    body: JSON.stringify({
                        sessionId: sessionId
                    })
                }
            );

            if (!response.ok) {
                console.warn(
                    "No se pudo registrar la salida en SQL Server.",
                    "HTTP:",
                    response.status
                );
            }
        }

    } catch (error) {

        console.warn(
            "Error de conexión al registrar la salida:",
            error
        );

    } finally {

        // Cerrar la sesión local aunque el backend falle.
        localStorage.removeItem("jwt_token");
        localStorage.removeItem("username");
        localStorage.removeItem("roles");
        localStorage.removeItem("sessionId");

        window.location.replace("./login.html");
    }
}


function mostrarMensaje(texto, tipo = "info") {
    mensaje.textContent = texto;
    mensaje.className = `mensaje mensaje-${tipo}`;
}

function limpiarMensaje() {
    mensaje.textContent = "";
    mensaje.className = "mensaje";
}


/* NAVEGACIÓN */

document.querySelectorAll(".menu-btn").forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".panel").forEach(
            panel => panel.classList.add("oculto")
        );

        const seccion = document.getElementById(
            button.dataset.seccion
        );

        if (seccion) {
            seccion.classList.remove("oculto");
        }

        document.querySelectorAll(".menu-btn").forEach(
            item => item.classList.remove("activo")
        );

        button.classList.add("activo");
        limpiarMensaje();

        if (window.innerWidth <= 850) {
            document.body.classList.remove(
                "menu-movil-abierto"
            );

            menuToggle?.setAttribute(
                "aria-expanded",
                "false"
            );
        }
    });
});


/*  UTILIDADES */

function ponerOpciones(
    select,
    datos,
    obtenerId,
    obtenerTexto,
    placeholder
) {
    select.innerHTML = "";

    const vacio = document.createElement("option");
    vacio.value = "";
    vacio.textContent = placeholder;
    select.appendChild(vacio);

    (datos || []).forEach(item => {
        const option = document.createElement("option");
        option.value = obtenerId(item);
        option.textContent = obtenerTexto(item);
        select.appendChild(option);
    });
}

function valor(objeto, ...nombres) {
    for (const nombre of nombres) {
        if (
            objeto
            && objeto[nombre] !== undefined
            && objeto[nombre] !== null
        ) {
            return objeto[nombre];
        }
    }

    return "";
}

function escaparHtml(valorTexto) {
    return String(valorTexto ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* CATÁLOGOS DE UBICACIÓN*/

async function cargarProvincias() {
    const select = document.getElementById("provincia");

    try {
        const data = await api("/catalogos/provincias");

        ponerOpciones(
            select,
            data,
            item => valor(item, "provinciaId", "ProvinciaID", "id"),
            item => valor(item, "nombre", "Nombre"),
            "Seleccione provincia..."
        );
    } catch (error) {
        ponerOpciones(select, [], () => "", () => "", "No disponible");
        mostrarMensaje("No fue posible cargar las provincias.", "error");
    }
}

async function cargarCantones(provinciaId) {
    const cantonSelect = document.getElementById("canton");
    const distritoSelect = document.getElementById("distrito");

    ponerOpciones(
        cantonSelect,
        [],
        () => "",
        () => "",
        "Seleccione cantón..."
    );

    ponerOpciones(
        distritoSelect,
        [],
        () => "",
        () => "",
        "Seleccione distrito..."
    );

    cantonSelect.disabled = true;
    distritoSelect.disabled = true;

    if (!provinciaId) {
        return;
    }

    try {
        const data = await api(
            `/catalogos/cantones?provinciaId=${encodeURIComponent(provinciaId)}`
        );

        ponerOpciones(
            cantonSelect,
            data,
            item => valor(item, "cantonId", "CantonID", "id"),
            item => valor(item, "nombre", "Nombre"),
            "Seleccione cantón..."
        );

        cantonSelect.disabled = false;
    } catch (error) {
        mostrarMensaje("No fue posible cargar los cantones.", "error");
    }
}

async function cargarDistritos(cantonId) {
    const select = document.getElementById("distrito");

    ponerOpciones(
        select,
        [],
        () => "",
        () => "",
        "Seleccione distrito..."
    );

    select.disabled = true;

    if (!cantonId) {
        return;
    }

    try {
        const data = await api(
            `/catalogos/distritos?cantonId=${encodeURIComponent(cantonId)}`
        );

        ponerOpciones(
            select,
            data,
            item => valor(item, "distritoId", "DistritoID", "id"),
            item => valor(item, "nombre", "Nombre"),
            "Seleccione distrito..."
        );

        select.disabled = false;
    } catch (error) {
        mostrarMensaje("No fue posible cargar los distritos.", "error");
    }
}

async function cargarActividadesEconomicas() {
    const select = document.getElementById("actividadEconomicaId");

    try {
        const data = await api("/catalogos/actividades-economicas");

        ponerOpciones(
            select,
            data,
            item => valor(
                item,
                "actividadEconomicaId",
                "ActividadEconomicaID",
                "id"
            ),
            item => {
                const codigo = valor(
                    item,
                    "codigoActividad",
                    "CodigoActividad",
                    "codigo"
                );

                const descripcion = valor(
                    item,
                    "descripcion",
                    "Descripcion",
                    "nombre"
                );

                return codigo
                    ? `${codigo} - ${descripcion}`
                    : descripcion;
            },
            "Seleccione actividad económica..."
        );
    } catch (error) {
        ponerOpciones(select, [], () => "", () => "", "No disponible");
    }
}

document.getElementById("provincia").addEventListener(
    "change",
    event => cargarCantones(event.target.value)
);

document.getElementById("canton").addEventListener(
    "change",
    event => cargarDistritos(event.target.value)
);


/* CRUD CLIENTES*/

let clientesCache = [];

function filtrarTablaClientes() {
    const q = (document.getElementById("buscarCliente")?.value || "").trim().toLocaleLowerCase();
    document.querySelectorAll("#tablaClientes tbody tr").forEach(fila => {
        const id = (fila.cells[1]?.textContent || "").toLocaleLowerCase();
        const nombre = (fila.cells[2]?.textContent || "").toLocaleLowerCase();
        fila.hidden = !(id.includes(q) || nombre.includes(q));
    });
}
document.getElementById("buscarCliente")?.addEventListener("input", filtrarTablaClientes);

async function cargarClientes() {

    try {


        const clientes = await api("/clientes");

        clientesCache = Array.isArray(clientes)
            ? clientes
            : [];

        // Cargar los clientes registrados en SQL Server
        // en el selector del formulario de facturas.
        cargarClientesFactura(clientesCache);


        if (clientesCache.length === 0) {

            document
                .getElementById("tablaClientes")
                .innerHTML = "";

            return;
        }


        const mostrarAcciones =
            esAdmin || esDigitador;


        document
            .getElementById("tablaClientes")
            .innerHTML = `
                <div class="tabla-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Identificación</th>
                                <th>Nombre</th>
                                <th>Provincia</th>
                                <th>Cantón</th>
                                <th>Distrito</th>
                                <th>Correo</th>
                                <th>Teléfono</th>
                                <th>Estado</th>
                                ${mostrarAcciones
                ? "<th>Acciones</th>"
                : ""
            }
                            </tr>
                        </thead>

                        <tbody>
                            ${clientesCache
                .map(
                    cliente => {

                        const clienteId =
                            Number(
                                valor(
                                    cliente,
                                    "clienteId",
                                    "ClienteID"
                                )
                            );


                        return `
                                                <tr>
                                                    <td>${escaparHtml(
                            clienteId
                        )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "numeroIdentificacion",
                                    "NumeroIdentificacion"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "nombre",
                                    "Nombre"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "provincia",
                                    "Provincia"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "canton",
                                    "Canton"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "distrito",
                                    "Distrito"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "correoElectronico",
                                    "CorreoElectronico"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "telefono",
                                    "Telefono"
                                )
                            )
                            }</td>

                                                    <td>${escaparHtml(
                                valor(
                                    cliente,
                                    "estado",
                                    "Estado"
                                )
                            )
                            }</td>

                                                    ${mostrarAcciones
                                ? `
                                                                <td class="acciones-tabla">

                                                                    <button
                                                                        type="button"
                                                                        class="btn-editar"
                                                                        onclick="editarClientePorId(${clienteId})"
                                                                    >
                                                                        Editar
                                                                    </button>

                                                                    ${esAdmin
                                    ? `
                                                                                <button
                                                                                    type="button"
                                                                                    class="btn-eliminar"
                                                                                    onclick="eliminarCliente(${clienteId})"
                                                                                >
                                                                                    Eliminar
                                                                                </button>
                                                                            `
                                    : ""
                                }

                                                                </td>
                                                            `
                                : ""
                            }
                                                </tr>
                                            `;
                    }
                )
                .join("")
            }
                        </tbody>
                    </table>
                </div>
            `;
        filtrarTablaClientes();

    } catch (error) {

        console.error("Error al cargar clientes:", error);

        document.getElementById("tablaClientes").innerHTML = "";

        // Limpiar el selector de facturas si falla la consulta.
        cargarClientesFactura([]);

        mostrarMensaje(
            "No fue posible cargar los clientes desde la base de datos.",
            "error"
        );
    }

}

async function editarClientePorId(id) {

    if (!esAdmin && !esDigitador) {
        return;
    }


    const cliente =
        clientesCache.find(
            item =>
                Number(
                    valor(
                        item,
                        "clienteId",
                        "ClienteID"
                    )
                ) === Number(id)
        );


    if (!cliente) {
        return;
    }


    await editarCliente(cliente);
}


document.getElementById("clienteForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        if (!esAdmin && !esDigitador) {
            mostrarMensaje(
                "Este usuario es solo de consulta.",
                "error"
            );
            return;
        }

        limpiarMensaje();

        const id = document.getElementById("clienteId").value;

        const dto = {
            nombre: document.getElementById("nombre").value.trim(),
            tipoIdentificacionId: Number(document.getElementById("tipoIdentificacion").value),
            numeroIdentificacion: document.getElementById("numeroIdentificacion").value.trim(),
            distritoId: parseInt(document.getElementById("distrito").value, 10),
            barrio: document.getElementById("barrio").value.trim(),
            otrasSenas: document.getElementById("otrasSenas").value.trim(),
            profesionOficio: document.getElementById("profesionOficio").value.trim(),
            actividadEconomicaId: parseInt(
                document.getElementById("actividadEconomicaId").value,
                10
            ),
            correoElectronico: document.getElementById("correoElectronico").value.trim(),
            telefono: document.getElementById("telefono").value.trim(),
            estado: document.getElementById("estado").value
        };

        try {
            await api(
                id ? `/clientes/${id}` : "/clientes",
                {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(dto)
                }
            );

            limpiarFormularioCliente();
            await cargarClientes();

            mostrarMensaje(
                id
                    ? "Cliente modificado correctamente."
                    : "Cliente registrado correctamente.",
                "exito"
            );
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

async function editarCliente(cliente) {

    if (!esAdmin && !esDigitador) {
        return;
    }

    document.getElementById("clienteId").value =
        valor(cliente, "clienteId", "ClienteID");

    document.getElementById("tipoIdentificacion").value =
        valor(cliente, "tipoIdentificacionId", "TipoIdentificacionID");

    document.getElementById("numeroIdentificacion").value =
        valor(cliente, "numeroIdentificacion", "NumeroIdentificacion");

    document.getElementById("nombre").value =
        valor(cliente, "nombre", "Nombre");

    document.getElementById("profesionOficio").value =
        valor(cliente, "profesionOficio", "ProfesionOficio");

    document.getElementById("correoElectronico").value =
        valor(cliente, "correoElectronico", "CorreoElectronico");

    document.getElementById("telefono").value =
        valor(cliente, "telefono", "Telefono");

    document.getElementById("barrio").value =
        valor(cliente, "barrio", "Barrio");

    document.getElementById("otrasSenas").value =
        valor(cliente, "otrasSenas", "OtrasSenas");

    document.getElementById("estado").value =
        valor(cliente, "estado", "Estado") || "A";

    const actividadId = valor(
        cliente,
        "actividadEconomicaId",
        "ActividadEconomicaID"
    );

    if (actividadId) {
        document.getElementById("actividadEconomicaId").value = actividadId;
    }

    const provinciaId = valor(cliente, "provinciaId", "ProvinciaID");
    const cantonId = valor(cliente, "cantonId", "CantonID");
    const distritoId = valor(cliente, "distritoId", "DistritoID");

    if (provinciaId) {
        document.getElementById("provincia").value = provinciaId;
        await cargarCantones(provinciaId);

        if (cantonId) {
            document.getElementById("canton").value = cantonId;
            await cargarDistritos(cantonId);

            if (distritoId) {
                document.getElementById("distrito").value = distritoId;
            }
        }
    }

    document.getElementById("btnGuardarCliente").textContent =
        "Actualizar cliente";

    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function eliminarCliente(id) {

    if (!esAdmin) {
        mostrarMensaje(
            "Solo el administrador puede eliminar clientes.",
            "error"
        );
        return;
    }

    const confirmar = confirm(
        "¿Desea eliminar este cliente?"
    );

    if (!confirmar) {
        return;
    }

    try {
        await api(
            `/clientes/${id}`,
            { method: "DELETE" }
        );

        await cargarClientes();
        mostrarMensaje("Cliente eliminado correctamente.", "exito");
    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

function limpiarFormularioCliente() {
    document.getElementById("clienteForm").reset();
    document.getElementById("clienteId").value = "";
    document.getElementById("btnGuardarCliente").textContent =
        "Guardar cliente";

    ponerOpciones(
        document.getElementById("canton"),
        [],
        () => "",
        () => "",
        "Seleccione cantón..."
    );

    ponerOpciones(
        document.getElementById("distrito"),
        [],
        () => "",
        () => "",
        "Seleccione distrito..."
    );

    document.getElementById("canton").disabled = true;
    document.getElementById("distrito").disabled = true;
}

document.getElementById("btnCancelarCliente").addEventListener(
    "click",
    limpiarFormularioCliente
);


/* CRUD CABYS*/

let cabysCache = [];

async function cargarCabysCrud() {
    try {
        const data = await api("/cabys");

        cabysCache =
            Array.isArray(data)
                ? data
                : [];

        document.getElementById("tablaCabysCrud").innerHTML =
            crearTablaConAcciones(
                data,
                [
                    ["cabysId", "ID"],
                    ["codigoCABYS", "Código CABYS"],
                    ["descripcion", "Descripción"],
                    ["estado", "Estado"]
                ],
                "editarCabys",
                "eliminarCabys",
                ["cabysId", "CabysID"],
                esAdmin,
                esAdmin
            );

        cargarCabysSelect(data);
    } catch (error) {
        document.getElementById("tablaCabysCrud").innerHTML = "";

        /* Si todavía no existe /api/cabys, probamos el endpoint
           de catálogo que ya habíamos definido. */
        try {
            const data = await api("/catalogos/cabys");
            cargarCabysSelect(data);
        } catch {
            ponerOpciones(
                document.getElementById("cabysId"),
                [],
                () => "",
                () => "",
                "No disponible"
            );
        }
    }
}

function cargarCabysSelect(data) {
    ponerOpciones(
        document.getElementById("cabysId"),
        data || [],
        item => valor(item, "codigoCABYS", "codigoCabys", "CodigoCABYS"),
        item => {
            const codigo = valor(
                item,
                "codigoCABYS",
                "codigoCabys",
                "CodigoCABYS"
            );

            const descripcion = valor(
                item,
                "descripcion",
                "Descripcion"
            );

            return `${codigo} - ${descripcion}`;
        },
        "Seleccione código CABYS..."
    );
}

document.getElementById("cabysForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        if (!esAdmin) {
            mostrarMensaje(
                "Solo el administrador puede modificar CABYS.",
                "error"
            );
            return;
        }

        const id = document.getElementById("cabysCrudId").value;

        const dto = {
            codigoCABYS:
                document.getElementById("codigoCabysCrud").value.trim(),
            descripcion:
                document.getElementById("descripcionCabysCrud").value.trim(),
            estado:
                document.getElementById("estadoCabysCrud").value
        };

        try {
            await api(
                id ? `/cabys/${id}` : "/cabys",
                {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(dto)
                }
            );

            limpiarCabys();
            await cargarCabysCrud();

            mostrarMensaje(
                id
                    ? "CABYS modificado correctamente."
                    : "CABYS registrado correctamente.",
                "exito"
            );
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

function editarCabysPorId(id) {

    const item =
        cabysCache.find(
            cabys =>
                Number(
                    valor(
                        cabys,
                        "cabysId",
                        "CabysID"
                    )
                ) === Number(id)
        );

    if (item) {
        editarCabys(item);
    }
}


function editarCabys(item) {

    if (!esAdmin) {
        return;
    }

    document.getElementById("cabysCrudId").value =
        valor(item, "cabysId", "CabysID");

    document.getElementById("codigoCabysCrud").value =
        valor(item, "codigoCABYS", "codigoCabys", "CodigoCABYS");

    document.getElementById("descripcionCabysCrud").value =
        valor(item, "descripcion", "Descripcion");

    document.getElementById("estadoCabysCrud").value =
        valor(item, "estado", "Estado") || "A";
}

async function eliminarCabys(id) {

    if (!esAdmin) {
        mostrarMensaje(
            "Solo el administrador puede eliminar CABYS.",
            "error"
        );
        return;
    }

    if (!confirm("¿Desea eliminar este código CABYS?")) {
        return;
    }

    try {
        await api(`/cabys/${id}`, { method: "DELETE" });
        await cargarCabysCrud();
        mostrarMensaje("CABYS eliminado correctamente.", "exito");
    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

function limpiarCabys() {
    document.getElementById("cabysForm").reset();
    document.getElementById("cabysCrudId").value = "";
}

document.getElementById("btnCancelarCabys").addEventListener(
    "click",
    limpiarCabys
);


/* CRUD PRODUCTOS / SERVICIOS */

let productosCache = [];

async function cargarProductos() {
    try {
        const data = await api("/productos");

        productosCache =
            Array.isArray(data)
                ? data
                : [];

        document.getElementById("tablaProductos").innerHTML =
            crearTablaConAcciones(
                data,
                [
                    ["productoServicioId", "ID"],
                    ["codigoCabys", "CABYS"],
                    ["descripcion", "Descripción"],
                    ["unidadMedida", "Unidad"],
                    ["precio", "Precio"],
                    ["estado", "Estado"]
                ],
                "editarProducto",
                "eliminarProducto",
                ["productoServicioId", "ProductoServicioID"],
                esAdmin,
                esAdmin
            );

        cargarProductosFactura(data);
    } catch (error) {
        document.getElementById("tablaProductos").innerHTML = "";
        ponerOpciones(
            document.getElementById("detalleProductoId"),
            [],
            () => "",
            () => "",
            "No disponible"
        );
    }
}

function cargarProductosFactura(data) {
    ponerOpciones(
        document.getElementById("detalleProductoId"),
        data || [],
        item => valor(
            item,
            "productoServicioId",
            "ProductoServicioID",
            "id"
        ),
        item => {
            const cabys = valor(
                item,
                "codigoCabys",
                "codigoCABYS",
                "CodigoCABYS"
            );

            const descripcion = valor(
                item,
                "descripcion",
                "Descripcion"
            );

            return cabys
                ? `${cabys} - ${descripcion}`
                : descripcion;
        },
        "Seleccione producto o servicio..."
    );
}

document.getElementById("productoForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        if (!esAdmin) {
            mostrarMensaje(
                "Solo el administrador puede modificar productos o servicios.",
                "error"
            );
            return;
        }

        const id =
            document.getElementById("productoServicioId").value;

        const dto = {
            codigoCabys:
                document.getElementById("cabysId").value,
            descripcion:
                document.getElementById("descripcionProducto").value.trim(),
            unidadMedidaId:
                Number(document.getElementById("unidadMedida").value),
            precio:
                parseFloat(document.getElementById("precio").value),
            tipoImpuestoId:
                parseInt(document.getElementById("tipoImpuestoId").value, 10),
            estado:
                document.getElementById("estadoProducto").value
        };

        try {
            await api(
                id ? `/productos/${id}` : "/productos",
                {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(dto)
                }
            );

            limpiarProducto();
            await cargarProductos();

            mostrarMensaje(
                id
                    ? "Producto / servicio modificado correctamente."
                    : "Producto / servicio registrado correctamente.",
                "exito"
            );
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

function editarProductoPorId(id) {

    const item =
        productosCache.find(
            producto =>
                Number(
                    valor(
                        producto,
                        "productoServicioId",
                        "ProductoServicioID"
                    )
                ) === Number(id)
        );

    if (item) {
        editarProducto(item);
    }
}


function editarProducto(item) {

    if (!esAdmin) {
        return;
    }

    document.getElementById("productoServicioId").value =
        valor(item, "productoServicioId", "ProductoServicioID");

    document.getElementById("cabysId").value =
        valor(item, "codigoCabys", "codigoCABYS", "CodigoCABYS");

    document.getElementById("descripcionProducto").value =
        valor(item, "descripcion", "Descripcion");

    document.getElementById("unidadMedida").value =
        valor(item, "unidadMedidaId", "UnidadMedidaID");

    document.getElementById("precio").value =
        valor(item, "precio", "Precio");

    document.getElementById("tipoImpuestoId").value =
        valor(item, "tipoImpuestoId", "TipoImpuestoID");

    document.getElementById("estadoProducto").value =
        valor(item, "estado", "Estado") || "A";
}

async function eliminarProducto(id) {

    if (!esAdmin) {
        mostrarMensaje(
            "Solo el administrador puede eliminar productos o servicios.",
            "error"
        );
        return;
    }

    if (!confirm("¿Desea eliminar este producto o servicio?")) {
        return;
    }

    try {
        await api(`/productos/${id}`, { method: "DELETE" });
        await cargarProductos();
        mostrarMensaje("Producto / servicio eliminado correctamente.", "exito");
    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

function limpiarProducto() {
    document.getElementById("productoForm").reset();
    document.getElementById("productoServicioId").value = "";
}

document.getElementById("btnCancelarProducto").addEventListener(
    "click",
    limpiarProducto
);



/* IMPUESTOS */

let impuestosCache = [];
let impuestoEditandoId = null;

// READ: cargar impuestos
async function cargarTiposImpuestoCombo() {
    try {
        const data = await api("/impuestos");

        impuestosCache = Array.isArray(data) ? data : [];

        ponerOpciones(
            document.getElementById("tipoImpuestoId"),
            impuestosCache.filter(item => item.estado === "A"),
            item => item.tipoImpuestoId,
            item => `${item.codigo} - ${item.descripcion}`,
            "Seleccione impuesto..."
        );

        document.getElementById("tablaImpuestos").innerHTML =
            crearTablaConAcciones(
                impuestosCache,
                [
                    ["codigo", "Código"],
                    ["descripcion", "Descripción"],
                    ["porcentaje", "Porcentaje"],
                    ["tarifa", "Tarifa"],
                    ["estado", "Estado"]
                ],
                "editarImpuesto",
                "eliminarImpuesto",
                ["tipoImpuestoId", "TipoImpuestoID"],
                esAdmin,
                esAdmin
            );

    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

// CREATE / UPDATE
document.getElementById("impuestoForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        if (!esAdmin) {
            mostrarMensaje(
                "Solo el administrador puede modificar impuestos.",
                "error"
            );
            return;
        }

        const dto = {
            codigo: document.getElementById("codigoImpuesto").value.trim(),
            descripcion: document.getElementById("descripcionImpuesto").value.trim(),
            porcentaje: Number(document.getElementById("porcentaje").value),
            tarifa: Number(document.getElementById("tarifa").value),
            estado: impuestoEditandoId === null
                ? "A"
                : impuestosCache.find(
                    item => item.tipoImpuestoId === impuestoEditandoId
                )?.estado || "A"
        };

        try {
            const editando = impuestoEditandoId !== null;

            await api(
                editando
                    ? `/impuestos/${impuestoEditandoId}`
                    : "/impuestos",
                {
                    method: editando ? "PUT" : "POST",
                    body: JSON.stringify(dto)
                }
            );

            limpiarFormularioImpuesto();
            await cargarTiposImpuestoCombo();

            mostrarMensaje(
                editando
                    ? "Impuesto actualizado correctamente."
                    : "Impuesto creado correctamente.",
                "exito"
            );

        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

// UPDATE: completar formulario
function editarImpuestoPorId(id) {
    if (!esAdmin) return;

    const impuesto = impuestosCache.find(
        item => Number(item.tipoImpuestoId) === Number(id)
    );

    if (!impuesto) return;

    impuestoEditandoId = Number(id);

    document.getElementById("codigoImpuesto").value =
        impuesto.codigo;

    document.getElementById("descripcionImpuesto").value =
        impuesto.descripcion;

    document.getElementById("porcentaje").value =
        impuesto.porcentaje;

    document.getElementById("tarifa").value =
        impuesto.tarifa;

    const boton = document.querySelector(
        '#impuestoForm button[type="submit"]'
    );

    if (boton) boton.textContent = "Actualizar impuesto";

    document.getElementById("impuestoForm")
        .scrollIntoView({ behavior: "smooth" });
}

// DELETE
async function eliminarImpuesto(id) {
    if (!esAdmin) return;

    if (!confirm("¿Desea eliminar este impuesto?")) {
        return;
    }

    try {
        await api(`/impuestos/${id}`, {
            method: "DELETE"
        });

        limpiarFormularioImpuesto();
        await cargarTiposImpuestoCombo();

        mostrarMensaje(
            "Impuesto eliminado correctamente.",
            "exito"
        );

    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

// Restablecer formulario
function limpiarFormularioImpuesto() {
    impuestoEditandoId = null;

    document.getElementById("impuestoForm").reset();

    const boton = document.querySelector(
        '#impuestoForm button[type="submit"]'
    );

    if (boton) boton.textContent = "Guardar impuesto";
}

// Cancelar edición
document.getElementById("btnCancelarImpuesto")
    ?.addEventListener(
        "click",
        limpiarFormularioImpuesto
    );



/* FACTURAS Y CAJA REGISTRADORA*/

let detalles = [];
let totalCobroActual = 0;
let productosCobro = [];
let impuestosCobro = [];
let registrandoCobro = false;

const obtenerCobro = id => document.getElementById(id);

function redondearCobro(n) {
    return Math.round((Number(n) + Number.EPSILON) * 100) / 100;
}

function monedaCobro() {
    const select = obtenerCobro("moneda");
    const texto = select.options[select.selectedIndex]
        ?.textContent?.toUpperCase() || "";

    return texto.includes("USD") || texto.includes("DÓLAR")
        ? "USD" : "CRC";
}

function formatoCobro(monto) {
    const codigo = monedaCobro();
    const simbolo = codigo === "USD" ? "$" : "₡";

    return simbolo + Number(monto || 0)
        .toLocaleString("es-CR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
}


// Acepta importes escritos como 65000, 65.000,00 o 65000.50.
function leerMontoCobro(texto) {
    let valor = String(texto ?? "").trim().replace(/[₡$\s]/g, "");
    if (!valor) return 0;
    const coma = valor.lastIndexOf(",");
    const punto = valor.lastIndexOf(".");
    if (coma > punto) {
        valor = valor.replace(/\./g, "").replace(",", ".");
    } else if (coma >= 0 && punto > coma) {
        valor = valor.replace(/,/g, "");
    }
    const numero = Number(valor);
    return Number.isFinite(numero) && numero >= 0 ? numero : NaN;
}

function formatearMontoEntrada() {
    const campo = obtenerCobro("montoRecibido");
    const cantidad = leerMontoCobro(campo.value);
    if (campo.value.trim() !== "" && Number.isFinite(cantidad)) {
        campo.value = cantidad.toLocaleString("es-CR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }
}

// El identificador 1 es Efectivo en la tabla dbo.MedioPago del proyecto.
function esPagoEfectivo() {
    return Number(obtenerCobro("medioPago").value) === 1;
}

async function cargarDatosCobro() {
    // Los importes se obtienen del catálogo real.
    // El backend vuelve a calcularlos al registrar.
    const [productos, impuestos] = await Promise.all([
        api("/productos"),
        api("/impuestos")
    ]);

    productosCobro = Array.isArray(productos) ? productos : [];
    impuestosCobro = Array.isArray(impuestos) ? impuestos : [];
}

function valorCobro(objeto, camel, pascal) {
    return objeto?.[camel] ?? objeto?.[pascal];
}

function calcularTotalCobro() {
    let subtotal = 0;
    let impuestos = 0;

    for (const detalle of detalles) {

        const producto = productosCobro.find(p =>
            Number(valorCobro(
                p, "productoServicioId", "ProductoServicioID"
            )) === Number(detalle.productoServicioId)
        );

        if (!producto) {
            throw new Error(
                "No se encontró el precio del producto: "
                + detalle.descripcion
            );
        }

        const precio = Number(valorCobro(
            producto, "precio", "Precio"
        ));

        const impuestoId = Number(valorCobro(
            producto, "tipoImpuestoId", "TipoImpuestoID"
        ));

        const impuesto = impuestosCobro.find(i =>
            Number(valorCobro(
                i, "tipoImpuestoId", "TipoImpuestoID"
            )) === impuestoId
        );

        if (!Number.isFinite(precio) || precio < 0 || !impuesto) {
            throw new Error(
                "Falta el precio o el impuesto de "
                + detalle.descripcion
            );
        }

        const porcentaje = Number(valorCobro(
            impuesto, "porcentaje", "Porcentaje"
        ));

        if (!Number.isFinite(porcentaje)) {
            throw new Error(
                "Porcentaje de impuesto inválido."
            );
        }

        // Aproximación para mostrar en la caja.
        // SQL Server conserva el cálculo definitivo.
        const linea = redondearCobro(
            precio * detalle.cantidad
        );

        subtotal += linea;
        impuestos += redondearCobro(
            linea * porcentaje / 100
        );
    }

    return redondearCobro(subtotal + impuestos);
}

/* AGREGAR PRODUCTOS */

obtenerCobro("agregarDetalle").addEventListener(
    "click", () => {

        const productoId = Number(
            obtenerCobro("detalleProductoId").value
        );

        const cantidad = Number(
            obtenerCobro("detalleCantidad").value
        );

        if (!Number.isInteger(productoId) || productoId <= 0
            || !Number.isInteger(cantidad) || cantidad < 1) {

            mostrarMensaje(
                "Seleccione un producto y una cantidad entera mayor a cero.",
                "error"
            );
            return;
        }

        const select = obtenerCobro("detalleProductoId");
        const descripcion =
            select.options[select.selectedIndex]?.textContent || "";

        detalles.push({
            productoServicioId: productoId,
            descripcion,
            cantidad
        });

        pintarDetallesFactura();

        select.value = "";
        obtenerCobro("detalleCantidad").value = "1";
        limpiarMensaje();
    }
);

function pintarDetallesFactura() {
    const contenedor = obtenerCobro("detallesFactura");

    if (detalles.length === 0) {
        contenedor.innerHTML = "";
        return;
    }

    contenedor.innerHTML = `
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>Producto / servicio</th>
                        <th>Cantidad</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    ${detalles.map((d, i) => `
                        <tr>
                            <td>${escaparHtml(d.descripcion)}</td>
                            <td>${d.cantidad}</td>
                            <td>
                                <button type="button"
                                        class="btn-eliminar"
                                        onclick="quitarDetalle(${i})">
                                    Quitar
                                </button>
                            </td>
                        </tr>
                    `).join("")}
                </tbody>
            </table>
        </div>
    `;
}

function quitarDetalle(indice) {
    detalles.splice(indice, 1);
    pintarDetallesFactura();
}

/* REGISTRO DE FACTURAS Y CAJA DE EFECTIVO */

obtenerCobro("facturaForm").addEventListener("submit", async event => {
    event.preventDefault();
    if (registrandoCobro) return;
    if (!esAdmin && !esDigitador) {
        mostrarMensaje("Este usuario es solo de consulta.", "error");
        return;
    }
    if (detalles.length === 0) {
        mostrarMensaje("Debe agregar al menos un producto.", "error");
        return;
    }
    if (!obtenerCobro("facturaForm").reportValidity()) return;

    try {
        await cargarDatosCobro();
        totalCobroActual = calcularTotalCobro();
        if (!Number.isFinite(totalCobroActual) || totalCobroActual <= 0) {
            throw new Error("El importe de la factura debe ser mayor a cero.");
        }

        // Tarjeta, SINPE, transferencia y demás: pago completo SIN abrir caja.
        if (!esPagoEfectivo()) {
            await registrarFacturaCobro(totalCobroActual);
            return;
        }

        // Únicamente para efectivo se abre la calculadora de vuelto.
        obtenerCobro("cobroMedioSeleccionado").textContent =
            obtenerCobro("medioPago").selectedOptions[0]?.textContent.trim()
            || "Efectivo";
        obtenerCobro("cobroCajero").textContent =
            obtenerCobro("usuarioActual")?.textContent.trim()
            || localStorage.getItem("username")
            || "Usuario autenticado";
        obtenerCobro("montoRecibido").value = "";
        obtenerCobro("cobroTotal").textContent = formatoCobro(totalCobroActual);
        obtenerCobro("cobroResumenTotal").textContent = formatoCobro(totalCobroActual);
        obtenerCobro("modalCobro").hidden = false;
        actualizarPantallaCobro();
        obtenerCobro("montoRecibido").focus();
    } catch (error) {
        console.error("No se pudo procesar la factura:", error);
        mostrarMensaje(error.message, "error");
    }
});

function actualizarPantallaCobro() {
    const recibido = leerMontoCobro(obtenerCobro("montoRecibido").value);
    const valido = Number.isFinite(recibido);
    const diferencia = valido
        ? redondearCobro(recibido - totalCobroActual)
        : -totalCobroActual;
    obtenerCobro("cobroEtiquetaDiferencia").textContent =
        diferencia < 0 ? "Falta:" : "Vuelto:";
    obtenerCobro("cobroDiferencia").textContent =
        formatoCobro(Math.abs(diferencia));
    obtenerCobro("cobroAviso").textContent = !valido
        ? "Ingrese un importe válido."
        : diferencia < 0
            ? "Ingrese el importe completo."
            : "Pago completo. Puede registrar la factura.";
    obtenerCobro("confirmarCobro").disabled =
        registrandoCobro || !valido || diferencia < 0;
}

obtenerCobro("montoRecibido").addEventListener("input", actualizarPantallaCobro);
obtenerCobro("montoRecibido").addEventListener("blur", formatearMontoEntrada);

obtenerCobro("cobroTeclado").addEventListener("click", event => {
    const boton = event.target.closest("button");
    if (!boton) return;
    const tecla = boton.dataset.tecla;
    const campo = obtenerCobro("montoRecibido");
    // Teclado para ingresar colones completos. Los decimales pueden
    // introducirse manualmente en el campo.
    let valor = campo.value.replace(/[^\d]/g, "");
    if (tecla === "C") valor = "";
    else if (tecla === "B") valor = valor.slice(0, -1);
    else valor += tecla;
    campo.value = valor;
    actualizarPantallaCobro();
});

async function registrarFacturaCobro(montoRecibido) {
    if (registrandoCobro) return;
    registrandoCobro = true;
    const boton = obtenerCobro("confirmarCobro");
    boton.disabled = true;

    let registrada = false;
    try {
        const resultado = await api("/facturas", {
            method: "POST",
            body: JSON.stringify({
                clienteId: Number(obtenerCobro("facturaClienteId").value),
                condicionVentaId: Number(obtenerCobro("condicionVenta").value),
                medioPagoId: Number(obtenerCobro("medioPago").value),
                monedaId: Number(obtenerCobro("moneda").value),
                tipoCambio: Number(obtenerCobro("tipoCambio").value),
                observaciones: obtenerCobro("observacionesFactura").value.trim(),
                montoRecibido: redondearCobro(montoRecibido),
                detalles: detalles.map(d => ({
                    productoServicioId: d.productoServicioId,
                    cantidad: d.cantidad
                }))
            })
        });
        if (!resultado?.facturaId) {
            throw new Error("No se recibió el identificador de la factura.");
        }
        registrada = true;
        habilitarPdfFactura(resultado.facturaId);
        obtenerCobro("modalCobro").hidden = true;
        limpiarFormularioFactura();
        mostrarMensaje(`Factura #${resultado.facturaId} registrada correctamente. Ya puede descargar el PDF.`, "exito");
        // Una falla al actualizar la tabla no debe confundirse con
        // una falla al registrar la factura (evita ventas duplicadas).
        await cargarFacturas();
    } catch (error) {
        console.error("Error al registrar factura:", error);
        if (!registrada) {
            obtenerCobro("cobroAviso").textContent = error.message;
            mostrarMensaje(error.message, "error");
        }
    } finally {
        registrandoCobro = false;
        if (!obtenerCobro("modalCobro").hidden) actualizarPantallaCobro();
        else boton.disabled = false;
    }
}

obtenerCobro("confirmarCobro").addEventListener("click", async () => {
    if (registrandoCobro) return;
    const recibido = leerMontoCobro(obtenerCobro("montoRecibido").value);
    if (!Number.isFinite(recibido) || recibido < totalCobroActual) {
        obtenerCobro("cobroAviso").textContent = "El monto recibido es insuficiente.";
        return;
    }
    await registrarFacturaCobro(recibido);
});

function cerrarCobro() {
    if (registrandoCobro) return;
    obtenerCobro("modalCobro").hidden = true;
}
obtenerCobro("cerrarModalCobro").addEventListener("click", cerrarCobro);
obtenerCobro("cancelarCobro").addEventListener("click", cerrarCobro);

function limpiarFormularioFactura() {
    obtenerCobro("facturaForm").reset();
    obtenerCobro("tipoCambio").value = "1";
    obtenerCobro("detalleCantidad").value = "1";
    detalles = [];
    pintarDetallesFactura();
}
obtenerCobro("btnCancelarFactura").addEventListener("click", () => {
    limpiarFormularioFactura();
    limpiarMensaje();
});

/* CONSULTAR FACTURAS */

async function cargarFacturas() {
    try {
        const data = await api("/facturas");
        cargarFacturasNota(data);
        obtenerCobro("tablaFacturas").innerHTML =
            crearTabla(data);
    } catch (error) {
        console.error("Error al cargar facturas:", error);
        obtenerCobro("tablaFacturas").innerHTML = "";
    }
}



/* NOTAS*/

document.getElementById("notaForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        if (!esAdmin && !esDigitador) {
            mostrarMensaje(
                "Este usuario es solo de consulta.",
                "error"
            );
            return;
        }

        try {
            await api("/notas", {
                method: "POST",
                body: JSON.stringify({
                    codigo:
                        document.getElementById("codigoNota").value.trim(),
                    facturaId:
                        parseInt(
                            document.getElementById("notaFacturaId").value,
                            10
                        ),
                    tipo:
                        document.getElementById("tipoNota").value,
                    fecha: document.getElementById("fechaNota").value,
                    monto: Number(document.getElementById("montoNota").value),
                    motivo:
                        document.getElementById("motivoNota").value.trim()
                })
            });

            document.getElementById("notaForm").reset();
            document.getElementById("fechaNota").value = new Date().toISOString().slice(0, 10);
            await cargarNotas();

            mostrarMensaje("Nota guardada correctamente.", "exito");
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

async function cargarNotas() {
    try {
        const data = await api("/notas");
        document.getElementById("tablaNotas").innerHTML =
            crearTabla(data);
    } catch {
        document.getElementById("tablaNotas").innerHTML = "";
    }
}


/*  REPORTES*/

async function reporteClientes() {
    ejecutarReporte(
        `/reportes/clientes?desde=${encodeURIComponent(
            document.getElementById("reporteDesde").value
        )
        }&hasta=${encodeURIComponent(
            document.getElementById("reporteHasta").value
        )
        }&estado=TODOS`
    );
}

async function reporteFacturas() {
    ejecutarReporte(
        `/reportes/facturas?desde=${encodeURIComponent(
            document.getElementById("reporteDesde").value
        )
        }&hasta=${encodeURIComponent(
            document.getElementById("reporteHasta").value
        )
        }`
    );
}

async function reporteAuditoria() {
    ejecutarReporte(
        `/reportes/auditoria?desde=${encodeURIComponent(
            document.getElementById("reporteDesde").value
        )
        }&hasta=${encodeURIComponent(
            document.getElementById("reporteHasta").value
        )
        }&usuario=TODOS`
    );
}

async function reporteSeguridad() {
    ejecutarReporte(
        "/reportes/seguridad?usuario=TODOS"
    );
}

async function ejecutarReporte(path) {
    try {
        const data = await api(path);
        document.getElementById("resultadoReporte").innerHTML =
            crearTabla(data);
    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}


/* TABLAS*/

function crearTabla(datos) {
    if (!Array.isArray(datos) || datos.length === 0) {
        return "";
    }

    const columnas = Object.keys(datos[0]);

    return `
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        ${columnas.map(
        columna =>
            `<th>${escaparHtml(columna)}</th>`
    ).join("")}
                    </tr>
                </thead>
                <tbody>
                    ${datos.map(fila => `
                        <tr>
                            ${columnas.map(
        columna =>
            `<td>${escaparHtml(
                fila[columna]
            )
            }</td>`
    ).join("")}
                        </tr>
                    `).join("")}
                </tbody>
            </table>
        </div>
    `;
}

function crearTablaConAcciones(
    datos,
    columnas,
    funcionEditar,
    funcionEliminar,
    posiblesId,
    permitirEditar = true,
    permitirEliminar = true
) {

    if (!Array.isArray(datos) || datos.length === 0) {
        return "";
    }


    const mostrarAcciones =
        permitirEditar || permitirEliminar;


    return `
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        ${columnas
            .map(
                columna =>
                    `<th>${escaparHtml(
                        columna[1]
                    )
                    }</th>`
            )
            .join("")
        }

                        ${mostrarAcciones
            ? "<th>Acciones</th>"
            : ""
        }
                    </tr>
                </thead>

                <tbody>
                    ${datos
            .map(
                item => {

                    const id =
                        Number(
                            valor(
                                item,
                                ...posiblesId
                            )
                        );


                    return `
                                        <tr>

                                            ${columnas
                            .map(
                                columna => `
                                                            <td>${escaparHtml(
                                    valor(
                                        item,
                                        columna[0],
                                        columna[0][0]
                                            .toUpperCase()
                                        + columna[0]
                                            .slice(1)
                                    )
                                )
                                    }</td>
                                                        `
                            )
                            .join("")
                        }

                                            ${mostrarAcciones
                            ? `
                                                        <td class="acciones-tabla">

                                                            ${permitirEditar
                                ? `
                                                                        <button
                                                                            type="button"
                                                                            class="btn-editar"
                                                                            onclick="${funcionEditar}PorId(${id})"
                                                                        >
                                                                            Editar
                                                                        </button>
                                                                    `
                                : ""
                            }

                                                            ${permitirEliminar
                                ? `
                                                                        <button
                                                                            type="button"
                                                                            class="btn-eliminar"
                                                                            onclick="${funcionEliminar}(${id})"
                                                                        >
                                                                            Eliminar
                                                                        </button>
                                                                    `
                                : ""
                            }

                                                        </td>
                                                    `
                            : ""
                        }

                                        </tr>
                                    `;
                }
            )
            .join("")
        }
                </tbody>
            </table>
        </div>
    `;
}



/* CATÁLOGOS DEL FORMULARIO: IDs REALES DE SQL SERVER */
async function cargarCatalogo(id, ruta, idKey, etiqueta) {
    const select = document.getElementById(id);
    if (!select) return;
    try {
        const data = await api(ruta);
        ponerOpciones(select, data, fila => fila[idKey], fila => etiqueta(fila), "Seleccione...");
    } catch (err) {
        console.error(`Error al cargar ${id}:`, err);
        ponerOpciones(select, [], () => "", () => "", "No disponible");
    }
}
async function cargarCatalogosAdicionales() {
    await Promise.all([
        cargarCatalogo("tipoIdentificacion", "/catalogos-extra/tipos-identificacion", "tipoIdentificacionId", x => `${x.codigo} - ${x.descripcion}`),
        cargarCatalogo("unidadMedida", "/catalogos-extra/unidades-medida", "unidadMedidaId", x => `${x.codigo} - ${x.descripcion}`),
        cargarCatalogo("condicionVenta", "/catalogos-extra/condiciones-venta", "condicionVentaId", x => `${x.codigo} - ${x.descripcion}`),
        cargarCatalogo("medioPago", "/catalogos-extra/medios-pago", "medioPagoId", x => `${x.codigo} - ${x.descripcion}`),
        cargarCatalogo("moneda", "/catalogos-extra/monedas", "monedaId", x => `${x.codigo} - ${x.descripcion}`)
    ]);
}
function cargarClientesFactura(clientes) {
    ponerOpciones(document.getElementById("facturaClienteId"),
        (clientes || []).filter(c => valor(c, "estado", "Estado") === "A"),
        c => valor(c, "clienteId", "ClienteID"),
        c => `${valor(c, "numeroIdentificacion", "NumeroIdentificacion")} - ${valor(c, "nombre", "Nombre")}`,
        "Seleccione cliente...");
}
function cargarFacturasNota(facturas) {
    ponerOpciones(document.getElementById("notaFacturaId"), facturas || [],
        f => valor(f, "facturaId", "FacturaID"),
        f => `Factura #${valor(f, "facturaId", "FacturaID")}`,
        "Seleccione factura...");
}


/* INICIO DEL SISTEMA */

async function iniciar() {

    configurarRol();

    document.getElementById("fechaNota").value =
        new Date().toISOString().slice(0, 10);

    console.log("Iniciando carga de datos desde SQL Server...");

    const tareas = [
        ["Catálogos adicionales", cargarCatalogosAdicionales],
        ["Provincias", cargarProvincias],
        ["Actividades económicas", cargarActividadesEconomicas],
        ["Clientes", cargarClientes],
        ["Facturas", cargarFacturas],
        ["Notas", cargarNotas],
        ["CABYS", cargarCabysCrud],
        ["Productos", cargarProductos],
        ["Impuestos", cargarTiposImpuestoCombo]
    ];

    const resultados = await Promise.allSettled(
        tareas.map(async ([nombre, funcion]) => {

            console.log("Cargando:", nombre);

            await funcion();

            console.log("Finalizó:", nombre);
        })
    );

    resultados.forEach((resultado, indice) => {
        if (resultado.status === "rejected") {
            console.error(
                "Falló la carga de " + tareas[indice][0],
                resultado.reason
            );
        }
    });

    console.log("Finalizó la carga inicial del sistema.");
}

iniciar().catch(error => {
    console.error("Error durante el inicio:", error);

    mostrarMensaje(
        "Ocurrió un error al iniciar el sistema. Revise la consola.",
        "error"
    );
});




/* DESCARGAR COMPROBANTE PDF */

let ultimaFacturaPdfId = null;

function habilitarPdfFactura(facturaId) {
    ultimaFacturaPdfId = Number(facturaId);

    const boton = document.getElementById(
        "btnDescargarFacturaPdf"
    );

    if (boton) {
        boton.disabled = !ultimaFacturaPdfId;
    }
}

document.getElementById("btnDescargarFacturaPdf")
    ?.addEventListener("click", async () => {

        if (!ultimaFacturaPdfId) {
            mostrarMensaje(
                "Primero debe registrar una factura.",
                "error"
            );
            return;
        }

        try {
            const response = await fetch(
                `${BASE}/facturas/${ultimaFacturaPdfId}/pdf`,
                {
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                }
            );


            if (!response.ok) {

                const detalleError = await response.text();

                console.error(
                    "Error al generar PDF:",
                    "Estado HTTP:", response.status,
                    "Respuesta del backend:", detalleError
                );

                throw new Error(
                    "Error al generar PDF (HTTP "
                    + response.status
                    + "). Revisa la terminal de Spring Boot."
                );
            }


            const blob = await response.blob();
            const url = URL.createObjectURL(blob);

            const enlace = document.createElement("a");
            enlace.href = url;
            enlace.download =
                `Factura_Propi_${ultimaFacturaPdfId}.pdf`;

            document.body.appendChild(enlace);
            enlace.click();
            enlace.remove();

            URL.revokeObjectURL(url);

        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    });

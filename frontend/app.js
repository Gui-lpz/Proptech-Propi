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
        cerrarSesion();
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

function cerrarSesion() {
    localStorage.clear();
    location.href = "./login.html";
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

async function cargarClientes() {

    try {

        const clientes =
            await api("/clientes");

        clientesCache =
            Array.isArray(clientes)
                ? clientes
                : [];


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
                                ${
                                    mostrarAcciones
                                        ? "<th>Acciones</th>"
                                        : ""
                                }
                            </tr>
                        </thead>

                        <tbody>
                            ${
                                clientesCache
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
                                                    <td>${
                                                        escaparHtml(
                                                            clienteId
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "numeroIdentificacion",
                                                                "NumeroIdentificacion"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "nombre",
                                                                "Nombre"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "provincia",
                                                                "Provincia"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "canton",
                                                                "Canton"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "distrito",
                                                                "Distrito"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "correoElectronico",
                                                                "CorreoElectronico"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "telefono",
                                                                "Telefono"
                                                            )
                                                        )
                                                    }</td>

                                                    <td>${
                                                        escaparHtml(
                                                            valor(
                                                                cliente,
                                                                "estado",
                                                                "Estado"
                                                            )
                                                        )
                                                    }</td>

                                                    ${
                                                        mostrarAcciones
                                                            ? `
                                                                <td class="acciones-tabla">

                                                                    <button
                                                                        type="button"
                                                                        class="btn-editar"
                                                                        onclick="editarClientePorId(${clienteId})"
                                                                    >
                                                                        Editar
                                                                    </button>

                                                                    ${
                                                                        esAdmin
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

    } catch (error) {

        document
            .getElementById("tablaClientes")
            .innerHTML = "";
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
            tipoIdentificacion: document.getElementById("tipoIdentificacion").value,
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
        valor(cliente, "tipoIdentificacion", "TipoIdentificacion");

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
        item => valor(item, "cabysId", "CabysID", "id"),
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
            cabysId:
                parseInt(document.getElementById("cabysId").value, 10),
            descripcion:
                document.getElementById("descripcionProducto").value.trim(),
            unidadMedida:
                document.getElementById("unidadMedida").value.trim(),
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
        valor(item, "cabysId", "CabysID");

    document.getElementById("descripcionProducto").value =
        valor(item, "descripcion", "Descripcion");

    document.getElementById("unidadMedida").value =
        valor(item, "unidadMedida", "UnidadMedida");

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


/* IMPUESTOS*/

async function cargarTiposImpuestoCombo() {
    try {
        const data = await api("/impuestos");

        ponerOpciones(
            document.getElementById("tipoImpuestoId"),
            data,
            item => valor(item, "tipoImpuestoId", "TipoImpuestoID", "id"),
            item => {
                const codigo = valor(item, "codigo", "Codigo");
                const descripcion = valor(item, "descripcion", "Descripcion");
                return `${codigo} - ${descripcion}`;
            },
            "Seleccione impuesto..."
        );

        document.getElementById("tablaImpuestos").innerHTML =
            crearTabla(data);
    } catch (error) {
        document.getElementById("tablaImpuestos").innerHTML = "";
    }
}

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
            codigo:
                document.getElementById("codigoImpuesto").value.trim(),
            descripcion:
                document.getElementById("descripcionImpuesto").value.trim(),
            porcentaje:
                parseFloat(document.getElementById("porcentaje").value),
            tarifa:
                parseFloat(document.getElementById("tarifa").value),
            estado: "A"
        };

        try {
            await api("/impuestos", {
                method: "POST",
                body: JSON.stringify(dto)
            });

            document.getElementById("impuestoForm").reset();
            await cargarTiposImpuestoCombo();

            mostrarMensaje("Impuesto guardado correctamente.", "exito");
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);


/*FACTURAS */

let detalles = [];

document.getElementById("agregarDetalle").addEventListener(
    "click",
    () => {
        const productoId = parseInt(
            document.getElementById("detalleProductoId").value,
            10
        );

        const cantidad = parseInt(
            document.getElementById("detalleCantidad").value,
            10
        );

        if (
            Number.isNaN(productoId)
            || Number.isNaN(cantidad)
            || cantidad < 1
            || !Number.isInteger(cantidad)
        ) {
            mostrarMensaje(
                "Seleccione un producto o servicio e ingrese una cantidad entera mayor o igual a 1.",
                "error"
            );

            return;
        }

        const select = document.getElementById("detalleProductoId");
        const descripcion =
            select.options[select.selectedIndex]?.textContent || "";

        detalles.push({
            productoServicioId: productoId,
            descripcion,
            cantidad
        });

        pintarDetallesFactura();

        document.getElementById("detalleProductoId").value = "";
        document.getElementById("detalleCantidad").value = "1";
        limpiarMensaje();
    }
);

function pintarDetallesFactura() {
    const contenedor =
        document.getElementById("detallesFactura");

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
                    ${detalles.map((detalle, indice) => `
                        <tr>
                            <td>${escaparHtml(detalle.descripcion)}</td>
                            <td>${detalle.cantidad}</td>
                            <td>
                                <button
                                    type="button"
                                    class="btn-eliminar"
                                    onclick="quitarDetalle(${indice})"
                                >
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

document.getElementById("facturaForm").addEventListener(
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

        if (detalles.length === 0) {
            mostrarMensaje(
                "Debe agregar al menos un detalle.",
                "error"
            );

            return;
        }

        try {
            await api("/facturas", {
                method: "POST",
                body: JSON.stringify({
                    clienteId:
                        parseInt(
                            document.getElementById("facturaClienteId").value,
                            10
                        ),
                    condicionVenta:
                        document.getElementById("condicionVenta").value,
                    medioPago:
                        document.getElementById("medioPago").value,
                    moneda:
                        document.getElementById("moneda").value,
                    tipoCambio:
                        parseFloat(
                            document.getElementById("tipoCambio").value
                        ),
                    observaciones:
                        document.getElementById("observacionesFactura").value.trim(),
                    detalles:
                        detalles.map(detalle => ({
                            productoServicioId:
                                detalle.productoServicioId,
                            cantidad:
                                detalle.cantidad
                        }))
                })
            });

            document.getElementById("facturaForm").reset();
            document.getElementById("tipoCambio").value = "1";
            document.getElementById("detalleCantidad").value = "1";

            detalles = [];
            pintarDetallesFactura();

            await cargarFacturas();

            mostrarMensaje(
                "Factura creada correctamente.",
                "exito"
            );
        } catch (error) {
            mostrarMensaje(error.message, "error");
        }
    }
);

document.getElementById("btnCancelarFactura").addEventListener(
    "click",
    () => {
        document.getElementById("facturaForm").reset();
        document.getElementById("tipoCambio").value = "1";
        document.getElementById("detalleCantidad").value = "1";
        detalles = [];
        pintarDetallesFactura();
        limpiarMensaje();
    }
);

async function cargarFacturas() {
    try {
        const data = await api("/facturas");
        document.getElementById("tablaFacturas").innerHTML =
            crearTabla(data);
    } catch {
        document.getElementById("tablaFacturas").innerHTML = "";
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
                    motivo:
                        document.getElementById("motivoNota").value.trim()
                })
            });

            document.getElementById("notaForm").reset();
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
        `/reportes/clientes?desde=${
            encodeURIComponent(
                document.getElementById("reporteDesde").value
            )
        }&hasta=${
            encodeURIComponent(
                document.getElementById("reporteHasta").value
            )
        }&estado=TODOS`
    );
}

async function reporteFacturas() {
    ejecutarReporte(
        `/reportes/facturas?desde=${
            encodeURIComponent(
                document.getElementById("reporteDesde").value
            )
        }&hasta=${
            encodeURIComponent(
                document.getElementById("reporteHasta").value
            )
        }`
    );
}

async function reporteAuditoria() {
    ejecutarReporte(
        `/reportes/auditoria?desde=${
            encodeURIComponent(
                document.getElementById("reporteDesde").value
            )
        }&hasta=${
            encodeURIComponent(
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
                                    `<td>${
                                        escaparHtml(
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
                        ${
                            columnas
                                .map(
                                    columna =>
                                        `<th>${
                                            escaparHtml(
                                                columna[1]
                                            )
                                        }</th>`
                                )
                                .join("")
                        }

                        ${
                            mostrarAcciones
                                ? "<th>Acciones</th>"
                                : ""
                        }
                    </tr>
                </thead>

                <tbody>
                    ${
                        datos
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

                                            ${
                                                columnas
                                                    .map(
                                                        columna => `
                                                            <td>${
                                                                escaparHtml(
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

                                            ${
                                                mostrarAcciones
                                                    ? `
                                                        <td class="acciones-tabla">

                                                            ${
                                                                permitirEditar
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

                                                            ${
                                                                permitirEliminar
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


/* INICIO*/

async function iniciar() {
    configurarRol();

    await Promise.all([
        cargarProvincias(),
        cargarActividadesEconomicas(),
        cargarClientes(),
        cargarFacturas(),
        cargarNotas()
    ]);


    await cargarCabysCrud();
    await cargarProductos();
    await cargarTiposImpuestoCombo();
}

iniciar();

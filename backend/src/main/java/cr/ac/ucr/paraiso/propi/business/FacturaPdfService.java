package cr.ac.ucr.paraiso.propi.business;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.dto.FacturaComprobanteDTO;
import cr.ac.ucr.paraiso.propi.dto.FacturaComprobanteDTO.Detalle;

@Service
public class FacturaPdfService {

        private final JdbcTemplate jdbcTemplate;

        public FacturaPdfService(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        public byte[] generar(Integer facturaId) throws IOException {

                FacturaComprobanteDTO factura = consultar(facturaId);

                if (factura == null) {
                        throw new IllegalArgumentException(
                                        "No existe la factura " + facturaId);
                }

                try (
                                PDDocument documento = new PDDocument();
                                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        PDPage pagina = new PDPage(PDRectangle.A4);
                        documento.addPage(pagina);

                        PDFont fuenteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                        PDFont fuenteNegrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                        Color turquesa = new Color(22, 166, 179);
                        Color violeta = new Color(67, 54, 135);
                        Color grisClaro = new Color(235, 242, 247);
                        Color grisTabla = new Color(225, 238, 243);
                        Color textoOscuro = new Color(30, 43, 70);

                        float anchoPagina = pagina.getMediaBox().getWidth();
                        float altoPagina = pagina.getMediaBox().getHeight();

                        try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {

                                // Fondo blanco
                                contenido.setNonStrokingColor(Color.WHITE);
                                contenido.addRect(0, 0, anchoPagina, altoPagina);
                                contenido.fill();

                                // Encabezado superior
                                contenido.setNonStrokingColor(turquesa);
                                contenido.addRect(0, altoPagina - 95, anchoPagina, 95);
                                contenido.fill();

                                escribir(contenido, fuenteNegrita, 24, 45, altoPagina - 45,
                                                "PROPTECH PROPI", Color.WHITE);

                                escribir(contenido, fuenteNormal, 12, 45, altoPagina - 67,
                                                "Sistema de Gestión de Facturación", Color.WHITE);

                                escribir(contenido, fuenteNegrita, 22, 45, altoPagina - 130,
                                                "COMPROBANTE DE VENTA", textoOscuro);

                                float y = altoPagina - 170;

                                // Bloque izquierdo de factura
                                escribir(contenido, fuenteNegrita, 13, 45, y,
                                                "Factura:", violeta);
                                escribir(contenido, fuenteNormal, 13, 115, y,
                                                texto(factura.getNumeroFactura()), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Clave:", violeta);
                                escribir(contenido, fuenteNormal, 11, 115, y,
                                                texto(factura.getClaveSimulada()), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Fecha y hora:", violeta);
                                escribir(contenido, fuenteNormal, 11, 115, y,
                                                formatearFecha(factura), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Cajero:", violeta);
                                escribir(contenido, fuenteNormal, 11, 115, y,
                                                texto(factura.getCajero()), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Forma de pago:", violeta);
                                escribir(contenido, fuenteNormal, 11, 115, y,
                                                texto(factura.getMedioPagoDescripcion()), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Moneda:", violeta);
                                escribir(contenido, fuenteNormal, 11, 115, y,
                                                texto(factura.getMonedaCodigo() + " - "
                                                                + factura.getMonedaDescripcion()),
                                                textoOscuro);

                                y -= 40;

                                escribir(contenido, fuenteNegrita, 16, 45, y,
                                                "DATOS DEL CLIENTE", violeta);

                                y -= 24;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Nombre:", textoOscuro);
                                escribir(contenido, fuenteNormal, 11, 105, y,
                                                texto(factura.getClienteNombre()), textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNegrita, 11, 45, y,
                                                "Identificación:", textoOscuro);
                                escribir(contenido, fuenteNormal, 11, 140, y,
                                                texto(factura.getClienteIdentificacion()), textoOscuro);

                                y -= 34;

                                // Encabezado de tabla
                                contenido.setNonStrokingColor(grisTabla);
                                contenido.addRect(40, y - 16, anchoPagina - 80, 22);
                                contenido.fill();

                                escribir(contenido, fuenteNegrita, 11, 50, y - 2,
                                                "PRODUCTO / SERVICIO", textoOscuro);
                                escribir(contenido, fuenteNegrita, 11, 360, y - 2,
                                                "CANT.", textoOscuro);
                                escribir(contenido, fuenteNegrita, 11, 425, y - 2,
                                                "P. UNIT.", textoOscuro);
                                escribir(contenido, fuenteNegrita, 11, 505, y - 2,
                                                "SUBTOTAL", textoOscuro);

                                y -= 36;

                                if (factura.getDetalles() != null) {
                                        for (Detalle detalle : factura.getDetalles()) {

                                                escribir(contenido, fuenteNormal, 10, 50, y,
                                                                ajustarTexto(detalle.getDescripcion(), 42),
                                                                textoOscuro);

                                                escribirDerecha(contenido, fuenteNormal, 10, 395, y,
                                                                numero(detalle.getCantidad()), textoOscuro);

                                                escribirDerecha(contenido, fuenteNormal, 10, 485, y,
                                                                dinero(detalle.getPrecioUnitario(), factura),
                                                                textoOscuro);

                                                escribirDerecha(contenido, fuenteNormal, 10, 575, y,
                                                                dinero(detalle.getMontoLinea(), factura), textoOscuro);

                                                y -= 18;
                                        }
                                }

                                y -= 10;

                                // Caja de resumen
                                float resumenX = 320;
                                float resumenY = y - 90;
                                float resumenAncho = 250;
                                float resumenAlto = 90;

                                contenido.setNonStrokingColor(new Color(233, 228, 245));
                                contenido.addRect(resumenX, resumenY, resumenAncho, resumenAlto);
                                contenido.fill();

                                escribir(contenido, fuenteNormal, 12, resumenX + 16, resumenY + 65,
                                                "Subtotal:", textoOscuro);
                                escribirDerecha(contenido, fuenteNegrita, 12, resumenX + 210, resumenY + 65,
                                                dinero(factura.getSubtotal(), factura), textoOscuro);

                                escribir(contenido, fuenteNormal, 12, resumenX + 16, resumenY + 42,
                                                "Impuestos:", textoOscuro);
                                escribirDerecha(contenido, fuenteNegrita, 12, resumenX + 210, resumenY + 42,
                                                dinero(factura.getTotalImpuestos(), factura), textoOscuro);

                                escribir(contenido, fuenteNegrita, 16, resumenX + 16, resumenY + 17,
                                                "TOTAL:", violeta);
                                escribirDerecha(contenido, fuenteNegrita, 16, resumenX + 210, resumenY + 17,
                                                dinero(factura.getTotal(), factura), violeta);

                                y = resumenY - 28;

                                escribir(contenido, fuenteNormal, 11, 45, y,
                                                "Monto recibido: " + dinero(factura.getMontoRecibido(), factura),
                                                textoOscuro);

                                y -= 20;
                                escribir(contenido, fuenteNormal, 11, 45, y,
                                                "Vuelto: " + dinero(factura.getVuelto(), factura),
                                                textoOscuro);

                                y -= 28;

                                if (factura.getObservaciones() != null
                                                && !factura.getObservaciones().isBlank()) {

                                        escribir(contenido, fuenteNegrita, 12, 45, y,
                                                        "Observaciones:", violeta);

                                        y -= 18;

                                        for (String linea : dividirTexto(factura.getObservaciones(), 70)) {
                                                escribir(contenido, fuenteNormal, 10, 45, y,
                                                                linea, textoOscuro);
                                                y -= 14;
                                        }
                                }

                                y -= 20;

                                escribir(contenido, fuenteNormal, 9, 45, y,
                                                "Documento generado por Proptech Propi.", new Color(90, 90, 90));

                                y -= 12;
                                escribir(contenido, fuenteNormal, 9, 45, y,
                                                "Comprobante emitido para control interno del sistema.",
                                                new Color(90, 90, 90));
                        }

                        documento.save(salida);
                        return salida.toByteArray();
                }
        }

        public FacturaComprobanteDTO consultar(Integer facturaId) {

                FacturaComprobanteDTO dto = jdbcTemplate.queryForObject(
                                """
                                                SELECT
                                                    f.FacturaID,
                                                    f.Fecha,
                                                    f.MonedaID,
                                                    m.Codigo AS MonedaCodigo,
                                                    m.Descripcion AS MonedaDescripcion,
                                                    f.MedioPagoID,
                                                    mp.Codigo AS MedioPagoCodigo,
                                                    mp.Descripcion AS MedioPagoDescripcion,
                                                    f.Subtotal,
                                                    f.TotalImpuestos,
                                                    f.MontoTotal,
                                                    f.Observaciones,
                                                    f.UsuarioRegistro,
                                                    f.MontoRecibido,
                                                    f.Vuelto,
                                                    c.Nombre AS ClienteNombre,
                                                    c.NumeroIdentificacion AS ClienteIdentificacion
                                                FROM dbo.Facturas f
                                                INNER JOIN dbo.Cliente c
                                                    ON c.ClienteID = f.ClienteID
                                                INNER JOIN dbo.Moneda m
                                                    ON m.MonedaID = f.MonedaID
                                                INNER JOIN dbo.MedioPago mp
                                                    ON mp.MedioPagoID = f.MedioPagoID
                                                WHERE f.FacturaID = ?
                                                """,
                                (rs, rowNum) -> {
                                        FacturaComprobanteDTO factura = new FacturaComprobanteDTO();

                                        factura.setFacturaId(rs.getInt("FacturaID"));
                                        factura.setNumeroFactura(
                                                        String.format("FAC-%08d", rs.getInt("FacturaID")));
                                        factura.setFecha(
                                                        rs.getTimestamp("Fecha").toLocalDateTime());

                                        factura.setMonedaId(rs.getInt("MonedaID"));
                                        factura.setMonedaCodigo(rs.getString("MonedaCodigo"));
                                        factura.setMonedaDescripcion(rs.getString("MonedaDescripcion"));

                                        factura.setMedioPagoId(rs.getInt("MedioPagoID"));
                                        factura.setMedioPagoCodigo(rs.getString("MedioPagoCodigo"));
                                        factura.setMedioPagoDescripcion(rs.getString("MedioPagoDescripcion"));

                                        factura.setSubtotal(valor(rs.getBigDecimal("Subtotal")));
                                        factura.setTotalImpuestos(valor(rs.getBigDecimal("TotalImpuestos")));
                                        factura.setTotal(valor(rs.getBigDecimal("MontoTotal")));

                                        factura.setObservaciones(rs.getString("Observaciones"));

                                        String cajero = rs.getString("UsuarioRegistro");
                                        factura.setCajero(
                                                        cajero == null || cajero.isBlank()
                                                                        ? "No registrado"
                                                                        : cajero);

                                        factura.setMontoRecibido(valor(rs.getBigDecimal("MontoRecibido")));
                                        factura.setVuelto(valor(rs.getBigDecimal("Vuelto")));

                                        factura.setClienteNombre(rs.getString("ClienteNombre"));
                                        factura.setClienteIdentificacion(rs.getString("ClienteIdentificacion"));

                                        factura.setClaveSimulada(generarClave(factura));

                                        return factura;
                                },
                                facturaId);

                if (dto == null) {
                        throw new IllegalArgumentException(
                                        "No se encontró la factura.");
                }

                List<Detalle> detalles = jdbcTemplate.query(
                                """
                                                SELECT
                                                    p.Descripcion,
                                                    d.Cantidad,
                                                    d.MontoLinea,
                                                    d.MontoImpuesto
                                                FROM dbo.FacturasDetalle d
                                                INNER JOIN dbo.ProductoServicio p
                                                    ON p.ProductoServicioID = d.ProductoServicioID
                                                WHERE d.FacturaID = ?
                                                ORDER BY d.DetalleID
                                                """,
                                (rs, rowNum) -> {
                                        Detalle detalle = new Detalle();

                                        BigDecimal cantidad = valor(rs.getBigDecimal("Cantidad"));
                                        BigDecimal montoLinea = valor(rs.getBigDecimal("MontoLinea"));

                                        detalle.setDescripcion(rs.getString("Descripcion"));
                                        detalle.setCantidad(cantidad);
                                        detalle.setMontoLinea(montoLinea);
                                        detalle.setMontoImpuesto(valor(rs.getBigDecimal("MontoImpuesto")));

                                        if (cantidad.compareTo(BigDecimal.ZERO) > 0) {
                                                detalle.setPrecioUnitario(
                                                                montoLinea.divide(cantidad, 2, RoundingMode.HALF_UP));
                                        } else {
                                                detalle.setPrecioUnitario(
                                                                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                                        }

                                        return detalle;
                                },
                                facturaId);

                dto.setDetalles(detalles);

                return dto;
        }

        private String generarClave(FacturaComprobanteDTO factura) {

                String fecha = factura.getFecha().format(
                                DateTimeFormatter.ofPattern("ddMMyyyy"));

                String consecutivo = String.format("%012d", factura.getFacturaId());

                return "506" + fecha + consecutivo + "0001";
        }

        private BigDecimal valor(BigDecimal valor) {
                if (valor == null) {
                        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                }
                return valor.setScale(2, RoundingMode.HALF_UP);
        }

        private String formatearFecha(FacturaComprobanteDTO factura) {
                if (factura.getFecha() == null) {
                        return "";
                }
                return factura.getFecha().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        private String dinero(BigDecimal monto, FacturaComprobanteDTO factura) {
                BigDecimal valor = monto == null
                                ? BigDecimal.ZERO
                                : monto.setScale(2, RoundingMode.HALF_UP);

                return simboloMoneda(factura) + formatearNumero(valor);
        }

        private String numero(BigDecimal valor) {
                BigDecimal n = valor == null
                                ? BigDecimal.ZERO
                                : valor.setScale(2, RoundingMode.HALF_UP);

                return formatearNumero(n);
        }

        private String simboloMoneda(FacturaComprobanteDTO factura) {

                if (factura == null
                                || factura.getMonedaCodigo() == null) {
                        return "";
                }

                String codigo = factura.getMonedaCodigo()
                                .trim()
                                .toUpperCase(Locale.ROOT);

                return switch (codigo) {
                        case "CRC" -> "CRC ";
                        case "USD" -> "$";
                        default -> codigo + " ";
                };
        }

        private String formatearNumero(BigDecimal numero) {
                return String.format(Locale.US, "%,.2f", numero);
        }

        private void escribir(
                        PDPageContentStream contenido,
                        PDFont fuente,
                        float tamano,
                        float x,
                        float y,
                        String texto,
                        Color color) throws IOException {

                contenido.beginText();
                contenido.setFont(fuente, tamano);
                contenido.setNonStrokingColor(color);
                contenido.newLineAtOffset(x, y);
                contenido.showText(texto(texto));
                contenido.endText();
        }

        private void escribirDerecha(
                        PDPageContentStream contenido,
                        PDFont fuente,
                        float tamano,
                        float xDerecha,
                        float y,
                        String texto,
                        Color color) throws IOException {

                String valor = texto(texto);
                float anchoTexto = fuente.getStringWidth(valor) / 1000 * tamano;
                float x = xDerecha - anchoTexto;

                escribir(contenido, fuente, tamano, x, y, valor, color);
        }

        private String texto(String valor) {
                if (valor == null || valor.isBlank()) {
                        return "";
                }

                String limpio = Normalizer.normalize(valor, Normalizer.Form.NFC);
                return limpio.replace("\n", " ").replace("\r", " ").trim();
        }

        private String ajustarTexto(String texto, int maximo) {
                String limpio = texto(texto);

                if (limpio.length() <= maximo) {
                        return limpio;
                }

                return limpio.substring(0, maximo - 3) + "...";
        }

        private List<String> dividirTexto(String texto, int longitudMaxima) {

                String limpio = texto(texto);

                java.util.ArrayList<String> lineas = new java.util.ArrayList<>();

                if (limpio.isBlank()) {
                        lineas.add("");
                        return lineas;
                }

                String[] palabras = limpio.split("\\s+");
                StringBuilder actual = new StringBuilder();

                for (String palabra : palabras) {

                        if (actual.length() == 0) {
                                actual.append(palabra);
                        } else if (actual.length() + 1 + palabra.length() <= longitudMaxima) {
                                actual.append(" ").append(palabra);
                        } else {
                                lineas.add(actual.toString());
                                actual = new StringBuilder(palabra);
                        }
                }

                if (actual.length() > 0) {
                        lineas.add(actual.toString());
                }

                return lineas;
        }
}

package cr.ac.ucr.paraiso.propi.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.NotaRepository;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;

@Service
public class NotaService {

    private final NotaRepository repository;

    // NUEVO: Catálogo de códigos de motivos de Hacienda
    private static final Map<String, String> MOTIVOS = Map.ofEntries(
            Map.entry("01", "Anula documento de referencia"),
            Map.entry("02", "Corrige monto"),
            Map.entry("04", "Referencia a otro documento"),
            Map.entry("05", "Sustituye comprobante provisional por contingencia"),
            Map.entry("06", "Devolución de mercancía"),
            Map.entry("07", "Sustituye comprobante electrónico"),
            Map.entry("08", "Factura endosada"),
            Map.entry("09", "Nota de crédito financiera"),
            Map.entry("10", "Nota de débito financiera"),
            Map.entry("11", "Proveedor no domiciliado"),
            Map.entry("12", "Crédito por exoneración posterior a la facturación"),
            Map.entry("13", "Anula documento por error material"),
            Map.entry("14", "Corrige monto por error material"),
            Map.entry("15", "Sustituye comprobante electrónico por error material"),
            Map.entry("16", "Sustituye comprobante electrónico rechazado"),
            Map.entry("99", "Otros"));

    public NotaService(NotaRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> listar() {
        return repository.listar();
    }

    public void guardar(NotaDTO dto, String usuario, String sesion) {
        System.out.println("1. NotaID recibido: " + dto.getNotaId());

        // 1. Generar el código SIEMPRE que sea un registro nuevo
        if (dto.getNotaId() == null || dto.getNotaId() <= 0) {
            long secuencial = repository.obtenerSiguienteSecuencial(dto.getTipo());
            String codigoHacienda = generarCodigoHacienda(1, 1, dto.getTipo(), secuencial);

            System.out.println("2. Código de Hacienda generado: " + codigoHacienda);

            dto.setCodigo(codigoHacienda);
            dto.setNotaId(null); // Asegura que a SQL llegue como NULL
        }

        // NUEVO: Colocar la descripción del motivo según su código.
        // Para el código 99 (Otros), conservar el texto escrito.
        if (dto.getCodigoMotivo() != null &&
                MOTIVOS.containsKey(dto.getCodigoMotivo()) &&
                !dto.getCodigoMotivo().equals("99")) {

            dto.setMotivo(MOTIVOS.get(dto.getCodigoMotivo()));
        }

        // 2. Validar los datos
        validar(dto);

        System.out.println("3. Código final antes de ir a BD: " + dto.getCodigo());

        // 3. Guardar en base de datos
        repository.guardar(dto);
    }

    public String generarCodigoHacienda(int sucursal, int terminal, String tipo, long numeroSecuencial) {
        // 03 = Nota de Crédito, 02 = Nota de Débito
        String tipoDocumento = tipo.equalsIgnoreCase("C") ? "03" : "02";

        // Formatea rellenando con ceros a la izquierda
        return String.format("%03d%05d%s%010d", sucursal, terminal, tipoDocumento, numeroSecuencial);
    }

    private void validar(NotaDTO request) {
        // La validación de null debe ir de primero para evitar NullPointerException
        if (request == null) {
            throw new IllegalArgumentException("Debe proporcionar los datos de la nota.");
        }

        if (request.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (request.getFacturaId() == null || request.getFacturaId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una factura válida.");
        }

        if (request.getTipo() == null || !(request.getTipo().equals("C") || request.getTipo().equals("D"))) {
            throw new IllegalArgumentException("El tipo debe ser C (crédito) o D (débito).");
        }

        // NUEVO: Verificar que el código del motivo exista
        if (request.getCodigoMotivo() == null ||
                !MOTIVOS.containsKey(request.getCodigoMotivo())) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un código de motivo válido de Hacienda.");
        }

        // NUEVO: Comprobar motivos financieros específicos
        if (request.getCodigoMotivo().equals("09") &&
                !request.getTipo().equals("C")) {
            throw new IllegalArgumentException(
                    "El motivo 09 corresponde a una nota de crédito.");
        }

        if (request.getCodigoMotivo().equals("10") &&
                !request.getTipo().equals("D")) {
            throw new IllegalArgumentException(
                    "El motivo 10 corresponde a una nota de débito.");
        }

        if (request.getCodigoMotivo().equals("12") &&
                !request.getTipo().equals("C")) {
            throw new IllegalArgumentException(
                    "El motivo 12 corresponde a una nota de crédito.");
        }

        if (request.getMotivo() == null || request.getMotivo().isBlank() || request.getMotivo().length() > 500) {
            throw new IllegalArgumentException("El motivo es obligatorio y no puede superar 500 caracteres.");
        }

        BigDecimal monto = request.getMonto();
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (request.getNotaId() != null && request.getNotaId() <= 0) {
            throw new IllegalArgumentException("El identificador de la nota no es válido.");
        }
    }
}

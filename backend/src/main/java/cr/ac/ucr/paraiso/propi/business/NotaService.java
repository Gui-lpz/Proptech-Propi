
package cr.ac.ucr.paraiso.propi.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.propi.data.NotaRepository;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;

@Service
public class NotaService {

    private final NotaRepository repository;

    // Catalogo de codigos de motivos de Hacienda
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

    /*
     * CONSTRUCTOR
     */

    public NotaService(NotaRepository repository) {
        this.repository = repository;
    }

    /* LISTAR NOTAS */

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        return repository.listar();
    }

    /* GUARDAR NOTA */

    @Transactional(rollbackFor = Exception.class)
    public void guardar(
            NotaDTO dto,
            String usuario,
            String sesion) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar los datos de la nota.");
        }

        System.out.println(
                "1. NotaID recibido: " + dto.getNotaId());

        // Validar el tipo antes de generar el codigo.
        if (dto.getTipo() == null ||
                (!dto.getTipo().equals("C") &&
                        !dto.getTipo().equals("D"))) {

            throw new IllegalArgumentException(
                    "El tipo debe ser C (crédito) o D (débito).");
        }

        // Generar codigo solo para registros nuevos.
        if (dto.getNotaId() == null ||
                dto.getNotaId() <= 0) {

            long secuencial = repository.obtenerSiguienteSecuencial(
                    dto.getTipo());

            String codigoHacienda = generarCodigoHacienda(
                    1,
                    1,
                    dto.getTipo(),
                    secuencial);

            System.out.println(
                    "2. Código de Hacienda generado: "
                            + codigoHacienda);

            dto.setCodigo(codigoHacienda);

            // Asegurar que SQL reciba NULL.
            dto.setNotaId(null);
        }

        // Asignar descripcion oficial del motivo.
        // Para el codigo 99 se conserva el texto.
        if (dto.getCodigoMotivo() != null &&
                MOTIVOS.containsKey(
                        dto.getCodigoMotivo())
                &&
                !dto.getCodigoMotivo().equals("99")) {

            dto.setMotivo(
                    MOTIVOS.get(dto.getCodigoMotivo()));
        }

        // Validaciones originales.
        validar(dto);

        System.out.println(
                "3. Código final antes de ir a BD: "
                        + dto.getCodigo());

        // Ejecutar procedimiento almacenado.
        repository.guardar(dto);
    }

    /*
     * 3. ACTUALIZAR NOTA
     * Utiliza el procedimiento:
     * dbo.sp_NotasCreditoDebito_Guardar
     * =
     */

    @Transactional(rollbackFor = Exception.class)
    public void actualizar(
            Integer notaId,
            NotaDTO dto,
            String usuario,
            String sesion) {

        if (notaId == null || notaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un NotaID válido.");
        }

        if (dto == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar los datos de la nota.");
        }

        // Asociar el ID recibido con la nota.
        dto.setNotaId(notaId);

        // Reutilizar las validaciones y el procedimiento
        // de guardar, que admite actualizaciones.
        guardar(dto, usuario, sesion);
    }

    /*
     * ELIMINAR NOTA
     * 
     * Utiliza:
     * dbo.sp_NotasCreditoDebito_Eliminar
     */

    @Transactional(rollbackFor = Exception.class)
    public void eliminar(Integer notaId) {

        if (notaId == null || notaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un NotaID válido.");
        }

        repository.eliminar(notaId);
    }

    /*
     * =====================================================
     * 5. GENERAR CODIGO DE HACIENDA
     * 
     * SE CONSERVA EL METODO ORIGINAL
     * =====================================================
     */

    public String generarCodigoHacienda(
            int sucursal,
            int terminal,
            String tipo,
            long numeroSecuencial) {

        // 03 = Nota de Credito
        // 02 = Nota de Debito

        String tipoDocumento = tipo.equalsIgnoreCase("C") ? "03" : "02";

        // Completar con ceros a la izquierda.
        return String.format(
                "%03d%05d%s%010d",
                sucursal,
                terminal,
                tipoDocumento,
                numeroSecuencial);
    }

    /*
     * =====================================================
     * 6. VALIDAR DATOS DE LA NOTA
     * 
     * SE CONSERVAN TODAS LAS VALIDACIONES
     * =====================================================
     */

    private void validar(NotaDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar los datos de la nota.");
        }

        // Validar fecha.
        if (request.getFecha() == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        // Validar factura.
        if (request.getFacturaId() == null ||
                request.getFacturaId() <= 0) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una factura válida.");
        }

        // Validar tipo de nota.
        if (request.getTipo() == null ||
                !(request.getTipo().equals("C") ||
                        request.getTipo().equals("D"))) {

            throw new IllegalArgumentException(
                    "El tipo debe ser C (crédito) o D (débito).");
        }

        // Verificar codigo de motivo.
        if (request.getCodigoMotivo() == null ||
                !MOTIVOS.containsKey(
                        request.getCodigoMotivo())) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un código de motivo "
                            + "válido de Hacienda.");
        }

        // Motivo 09: solo credito.
        if (request.getCodigoMotivo().equals("09") &&
                !request.getTipo().equals("C")) {

            throw new IllegalArgumentException(
                    "El motivo 09 corresponde a una nota de crédito.");
        }

        // Motivo 10: solo debito.
        if (request.getCodigoMotivo().equals("10") &&
                !request.getTipo().equals("D")) {

            throw new IllegalArgumentException(
                    "El motivo 10 corresponde a una nota de débito.");
        }

        // Motivo 12: solo credito.
        if (request.getCodigoMotivo().equals("12") &&
                !request.getTipo().equals("C")) {

            throw new IllegalArgumentException(
                    "El motivo 12 corresponde a una nota de crédito.");
        }

        // Validar descripcion del motivo.
        if (request.getMotivo() == null ||
                request.getMotivo().isBlank() ||
                request.getMotivo().length() > 500) {

            throw new IllegalArgumentException(
                    "El motivo es obligatorio y no puede "
                            + "superar 500 caracteres.");
        }

        // Validar monto.
        BigDecimal monto = request.getMonto();

        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero.");
        }

        // Validar identificador.
        if (request.getNotaId() != null &&
                request.getNotaId() <= 0) {

            throw new IllegalArgumentException(
                    "El identificador de la nota no es válido.");
        }
    }
}

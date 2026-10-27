
package cr.ac.ucr.paraiso.propi.business;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.propi.data.ReporteRepository;

@Service
public class ReporteService {

    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    // VALIDAR RANGO DE FECHAS
    private void validarFechas(
            LocalDate desde,
            LocalDate hasta) {

        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Debe indicar las dos fechas.");
        }

        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha desde no puede ser mayor que la fecha hasta.");
        }
    }

    // REPORTE DE CLIENTES
    @Transactional(readOnly = true)
    public List<Map<String, Object>> clientes(
            LocalDate desde,
            LocalDate hasta,
            String estado) {

        validarFechas(desde, hasta);

        return repository.clientes(
                desde,
                hasta,
                estado);
    }

    // REPORTE DE FACTURAS
    @Transactional(readOnly = true)
    public List<Map<String, Object>> facturas(
            LocalDate desde,
            LocalDate hasta) {

        validarFechas(desde, hasta);

        return repository.facturas(
                desde,
                hasta);
    }

    // REPORTE DE AUDITORIA
    @Transactional(readOnly = true)
    public List<Map<String, Object>> auditoria(
            LocalDate desde,
            LocalDate hasta,
            String usuario) {

        validarFechas(desde, hasta);

        return repository.auditoria(
                desde,
                hasta,
                usuario);
    }

    // REPORTE DE SEGURIDAD
    @Transactional(readOnly = true)
    public List<Map<String, Object>> seguridad(
            String usuario) {

        return repository.seguridad(usuario);
    }
}

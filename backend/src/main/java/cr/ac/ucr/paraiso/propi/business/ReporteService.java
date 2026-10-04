package cr.ac.ucr.paraiso.propi.business;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.ReporteRepository;

@Service
public class ReporteService {

    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> clientes(
            LocalDate desde,
            LocalDate hasta,
            String estado) {
        return repository.clientes(desde, hasta, estado);
    }

    public List<Map<String, Object>> facturas(
            LocalDate desde,
            LocalDate hasta) {
        return repository.facturas(desde, hasta);
    }

    public List<Map<String, Object>> auditoria(
            LocalDate desde,
            LocalDate hasta,
            String usuario) {
        return repository.auditoria(desde, hasta, usuario);
    }

    public List<Map<String, Object>> seguridad(
            String usuario) {
        return repository.seguridad(usuario);
    }
}

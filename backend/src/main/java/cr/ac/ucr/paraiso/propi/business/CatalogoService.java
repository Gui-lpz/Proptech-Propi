package cr.ac.ucr.paraiso.propi.business;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.CatalogoRepository;

@Service
public class CatalogoService {

        private final CatalogoRepository repository;

        public CatalogoService(
                        CatalogoRepository repository) {

                this.repository = repository;
        }

        public List<Map<String, Object>> listarProvincias() {

                return repository
                                .listarProvincias();
        }

        public List<Map<String, Object>> listarCantones(
                        Integer provinciaId) {

                return repository
                                .listarCantones(
                                                provinciaId);
        }

        public List<Map<String, Object>> listarDistritos(
                        Integer cantonId) {

                return repository
                                .listarDistritos(
                                                cantonId);
        }

        public List<Map<String, Object>> listarActividadesEconomicas() {

                return repository
                                .listarActividadesEconomicas();
        }

        public List<Map<String, Object>> listarCabys() {

                return repository
                                .listarCabys();
        }
}
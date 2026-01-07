package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia;
import ar.utn.ba.ddsi.agregador.repositorie.IProvinciaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvinciaService {

    private final IProvinciaRepository provinciaRepository;
    private final GeocodificacionService geocodificacionService;

    @PostConstruct
    @Transactional
    public void inicializarProvincias() {
        long count = provinciaRepository.count();

        if (count == 0) {
            log.info("[ProvinciaService] Inicializando provincias argentinas...");

            List<String> provinciasArgentinas = Arrays.asList(
                "Buenos Aires",
                "Ciudad Autónoma de Buenos Aires",
                "Catamarca",
                "Chaco",
                "Chubut",
                "Córdoba",
                "Corrientes",
                "Entre Ríos",
                "Formosa",
                "Jujuy",
                "La Pampa",
                "La Rioja",
                "Mendoza",
                "Misiones",
                "Neuquén",
                "Río Negro",
                "Salta",
                "San Juan",
                "San Luis",
                "Santa Cruz",
                "Santa Fe",
                "Santiago del Estero",
                "Tierra del Fuego",
                "Tucumán",
                "Provincia Desconocida"
            );

            for (String nombreProvincia : provinciasArgentinas) {
                Provincia provincia = new Provincia(nombreProvincia);
                provinciaRepository.save(provincia);
            }

            log.info("[ProvinciaService] {} provincias inicializadas correctamente", provinciasArgentinas.size());
        } else {
            log.debug("[ProvinciaService] Provincias ya inicializadas (total: {})", count);
        }
    }

    @Transactional
    public Provincia obtenerProvinciaPorCoordenadas(Double latitud, Double longitud) {
        String nombreProvincia = geocodificacionService.obtenerProvinciaPorCoordenadas(latitud, longitud);
        return obtenerOCrearProvincia(nombreProvincia);
    }

    @Transactional
    public Provincia obtenerOCrearProvincia(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            nombre = "Provincia Desconocida";
        }

        Optional<Provincia> provinciaOpt = provinciaRepository.findByNombre(nombre);

        if (provinciaOpt.isPresent()) {
            return provinciaOpt.get();
        }

        log.info("[ProvinciaService] Creando nueva provincia: {}", nombre);
        Provincia nuevaProvincia = new Provincia(nombre);
        return provinciaRepository.save(nuevaProvincia);
    }

    public ResponseEntity<List<String>> getProvinciasConHechos() {
        log.info("[ProvinciaService] Obteniendo provincias con hechos");
        List<String> provincias = provinciaRepository.findProvinciasConHechos();
        log.debug("[ProvinciaService] Se encontraron {} provincias con hechos", provincias.size());
        return ResponseEntity.ok(provincias);
    }
}


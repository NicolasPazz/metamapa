package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.domain.provincia.Provincia;
import ar.utn.ba.ddsi.estadistica.repository.ProvinciaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeolocalizacionService {

  private final ProvinciaRepository provinciaRepository;

  @PostConstruct
  public void inicializarProvincias() {
    log.info("🗺️ [GeolocalizacionService] Inicializando provincias argentinas...");

    if (provinciaRepository.count() > 0) {
      log.info("✅ [GeolocalizacionService] Las provincias ya están cargadas en la BD");
      return;
    }

    List<Provincia> provincias = List.of(
        Provincia.builder().nombre("Jujuy").latitudCentro(-24.1858).longitudCentro(-65.2995).radioKm(150.0).build(),
        Provincia.builder().nombre("Salta").latitudCentro(-24.7859).longitudCentro(-65.4117).radioKm(200.0).build(),
        Provincia.builder().nombre("Tucumán").latitudCentro(-26.8083).longitudCentro(-65.2176).radioKm(100.0).build(),
        Provincia.builder().nombre("Santiago del Estero").latitudCentro(-27.7824).longitudCentro(-64.2643).radioKm(200.0).build(),
        Provincia.builder().nombre("Catamarca").latitudCentro(-28.4696).longitudCentro(-65.7852).radioKm(180.0).build(),
        Provincia.builder().nombre("La Rioja").latitudCentro(-29.4131).longitudCentro(-66.8558).radioKm(150.0).build(),
        Provincia.builder().nombre("Formosa").latitudCentro(-26.1858).longitudCentro(-58.1756).radioKm(180.0).build(),
        Provincia.builder().nombre("Chaco").latitudCentro(-27.4506).longitudCentro(-59.0231).radioKm(150.0).build(),
        Provincia.builder().nombre("Corrientes").latitudCentro(-27.4692).longitudCentro(-58.8306).radioKm(130.0).build(),
        Provincia.builder().nombre("Misiones").latitudCentro(-27.3671).longitudCentro(-55.9000).radioKm(100.0).build(),
        Provincia.builder().nombre("San Juan").latitudCentro(-31.5375).longitudCentro(-68.5364).radioKm(120.0).build(),
        Provincia.builder().nombre("Mendoza").latitudCentro(-32.8895).longitudCentro(-68.8458).radioKm(180.0).build(),
        Provincia.builder().nombre("San Luis").latitudCentro(-33.2956).longitudCentro(-66.3356).radioKm(150.0).build(),
        Provincia.builder().nombre("Córdoba").latitudCentro(-31.4201).longitudCentro(-64.1888).radioKm(200.0).build(),
        Provincia.builder().nombre("Santa Fe").latitudCentro(-31.6333).longitudCentro(-60.7000).radioKm(180.0).build(),
        Provincia.builder().nombre("Entre Ríos").latitudCentro(-31.7333).longitudCentro(-60.5333).radioKm(150.0).build(),
        Provincia.builder().nombre("Buenos Aires").latitudCentro(-36.6769).longitudCentro(-60.5588).radioKm(300.0).build(),
        Provincia.builder().nombre("Ciudad Autónoma de Buenos Aires").latitudCentro(-34.6037).longitudCentro(-58.3816).radioKm(20.0).build(),
        Provincia.builder().nombre("La Pampa").latitudCentro(-36.6167).longitudCentro(-64.2833).radioKm(180.0).build(),
        Provincia.builder().nombre("Neuquén").latitudCentro(-38.9516).longitudCentro(-68.0591).radioKm(180.0).build(),
        Provincia.builder().nombre("Río Negro").latitudCentro(-40.8135).longitudCentro(-63.0000).radioKm(220.0).build(),
        Provincia.builder().nombre("Chubut").latitudCentro(-43.2951).longitudCentro(-65.1029).radioKm(250.0).build(),
        Provincia.builder().nombre("Santa Cruz").latitudCentro(-48.8155).longitudCentro(-69.9641).radioKm(300.0).build(),
        Provincia.builder().nombre("Tierra del Fuego").latitudCentro(-54.8019).longitudCentro(-68.3029).radioKm(150.0).build()
    );

    provinciaRepository.saveAll(provincias);
    log.info("✅ [GeolocalizacionService] {} provincias cargadas exitosamente", provincias.size());
  }

  public String obtenerProvincia(Double latitud, Double longitud) {
    if (latitud == null || longitud == null) {
      log.warn("⚠️ [GeolocalizacionService] Coordenadas nulas, retornando 'Desconocida'");
      return "Desconocida";
    }

    List<Provincia> todasLasProvincias = provinciaRepository.findAll();

    if (todasLasProvincias.isEmpty()) {
      log.warn("⚠️ [GeolocalizacionService] No hay provincias en la BD");
      return "Desconocida";
    }

    Provincia provinciaMasCercana = null;
    double distanciaMinima = Double.MAX_VALUE;

    for (Provincia provincia : todasLasProvincias) {
      double distancia = calcularDistanciaHaversine(
          latitud, longitud,
          provincia.getLatitudCentro(), provincia.getLongitudCentro()
      );

      if (distancia < distanciaMinima) {
        distanciaMinima = distancia;
        provinciaMasCercana = provincia;
      }
    }

    return provinciaMasCercana != null ? provinciaMasCercana.getNombre() : "Desconocida";
  }

  private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371;

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}


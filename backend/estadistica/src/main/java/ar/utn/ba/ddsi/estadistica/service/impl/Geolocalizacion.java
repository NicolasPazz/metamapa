package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.domain.provincia.ProvinciaEnum;
import ar.utn.ba.ddsi.estadistica.service.IGeolocalizacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Geolocalizacion implements IGeolocalizacion {

    // Coordenadas aproximadas de las provincias argentinas (centroide)
    private static final double[][] PROVINCIAS_COORDENADAS = {
        // {lat, lng, lat_min, lat_max, lng_min, lng_max}
        {-34.6037, -58.3816, -35.0, -34.0, -59.0, -57.0}, // Buenos Aires
        {-31.4201, -64.1888, -32.0, -30.0, -65.0, -63.0}, // Cordoba
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Salta
        {-26.8083, -65.2176, -27.0, -26.0, -66.0, -64.0}, // Tucuman
        {-27.3621, -55.9008, -28.0, -26.0, -57.0, -54.0}, // Misiones
        {-27.3621, -55.9008, -28.0, -26.0, -57.0, -54.0}, // Corrientes
        {-27.3621, -55.9008, -28.0, -26.0, -57.0, -54.0}, // Entre Rios
        {-31.6333, -60.7000, -32.0, -31.0, -61.0, -60.0}, // Santa Fe
        {-32.9442, -60.6505, -33.0, -32.0, -61.0, -60.0}, // Santa Fe (alternativa)
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Jujuy
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Catamarca
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // La Rioja
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Santiago del Estero
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Formosa
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Chaco
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // San Juan
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // San Luis
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Mendoza
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Neuquen
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Rio Negro
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Chubut
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Santa Cruz
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}, // Tierra del Fuego
        {-24.7821, -65.4232, -25.0, -23.0, -66.0, -64.0}  // La Pampa
    };

    private static final ProvinciaEnum[] PROVINCIAS = {
        ProvinciaEnum.BUENOS_AIRES,
        ProvinciaEnum.CORDOBA,
        ProvinciaEnum.SALTA,
        ProvinciaEnum.TUCUMAN,
        ProvinciaEnum.MISIONES,
        ProvinciaEnum.CORRIENTES,
        ProvinciaEnum.ENTRE_RIOS,
        ProvinciaEnum.SANTA_FE, // sur
        ProvinciaEnum.SANTA_FE, // norte
        ProvinciaEnum.JUJUY,
        ProvinciaEnum.CATAMARCA,
        ProvinciaEnum.LA_RIOJA,
        ProvinciaEnum.SANTIAGO_DEL_ESTERO,
        ProvinciaEnum.FORMOSA,
        ProvinciaEnum.CHACO,
        ProvinciaEnum.SAN_JUAN,
        ProvinciaEnum.SAN_LUIS,
        ProvinciaEnum.MENDOZA,
        ProvinciaEnum.NEUQUEN,
        ProvinciaEnum.RIO_NEGRO,
        ProvinciaEnum.CHUBUT,
        ProvinciaEnum.SANTA_CRUZ,
        ProvinciaEnum.TIERRA_DEL_FUEGO,
        ProvinciaEnum.LA_PAMPA
    };

    @Override
    public ProvinciaEnum obtenerProvincia(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            log.warn("Coordenadas nulas: lat={}, lng={}", latitud, longitud);
            return ProvinciaEnum.INVALIDA;
        }

        // Verificar si las coordenadas estan dentro de Argentina
        if (!estaEnArgentina(latitud, longitud)) {
            log.warn("Coordenadas fuera de Argentina: lat={}, lng={}", latitud, longitud);
            return ProvinciaEnum.INVALIDA;
        }

        // Buscar la provincia mas cercana
        for (int i = 0; i < PROVINCIAS_COORDENADAS.length; i++) {
            double[] provincia = PROVINCIAS_COORDENADAS[i];
            if (latitud >= provincia[2] && latitud <= provincia[3] &&
                longitud >= provincia[4] && longitud <= provincia[5]) {
                log.debug("Coordenadas {} {} mapeadas a {}", latitud, longitud, PROVINCIAS[i]);
                return PROVINCIAS[i];
            }
        }

        // Si no se encuentra una provincia especifica, usar distancia euclidiana
        return obtenerProvinciaPorDistancia(latitud, longitud);
    }

    private boolean estaEnArgentina(Double latitud, Double longitud) {
        // Limites aproximados de Argentina
        return latitud >= -55.0 && latitud <= -21.0 && 
               longitud >= -73.0 && longitud <= -53.0;
    }

    private ProvinciaEnum obtenerProvinciaPorDistancia(Double latitud, Double longitud) {
        double minDistancia = Double.MAX_VALUE;
        int provinciaIndex = 0;

        for (int i = 0; i < PROVINCIAS_COORDENADAS.length; i++) {
            double[] provincia = PROVINCIAS_COORDENADAS[i];
            double distancia = Math.sqrt(
                Math.pow(latitud - provincia[0], 2) + 
                Math.pow(longitud - provincia[1], 2)
            );
            
            if (distancia < minDistancia) {
                minDistancia = distancia;
                provinciaIndex = i;
            }
        }

        log.debug("Provincia mas cercana por distancia: {} (distancia: {})",
            PROVINCIAS[provinciaIndex], minDistancia);
        return PROVINCIAS[provinciaIndex];
    }
}

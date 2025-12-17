package ar.utn.ba.ddsi.agregador.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GeocodificacionService {

    // Diccionario de normalización de nombres de provincias
    private static final Map<String, String> NORMALIZACION_PROVINCIAS = new HashMap<>();

    static {
        // Buenos Aires
        NORMALIZACION_PROVINCIAS.put("buenos aires", "Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("provincia de buenos aires", "Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("bs as", "Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("pcia de bs as", "Buenos Aires");

        // Ciudad Autónoma de Buenos Aires
        NORMALIZACION_PROVINCIAS.put("caba", "Ciudad Autónoma de Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("capital federal", "Ciudad Autónoma de Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("ciudad autonoma de buenos aires", "Ciudad Autónoma de Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("c.a.b.a", "Ciudad Autónoma de Buenos Aires");

        // Córdoba
        NORMALIZACION_PROVINCIAS.put("cordoba", "Córdoba");
        NORMALIZACION_PROVINCIAS.put("córdoba", "Córdoba");

        // Santa Fe
        NORMALIZACION_PROVINCIAS.put("santa fe", "Santa Fe");
        NORMALIZACION_PROVINCIAS.put("provincia de santa fe", "Santa Fe");

        // Mendoza
        NORMALIZACION_PROVINCIAS.put("mendoza", "Mendoza");

        // Tucumán
        NORMALIZACION_PROVINCIAS.put("tucuman", "Tucumán");
        NORMALIZACION_PROVINCIAS.put("tucumán", "Tucumán");
        NORMALIZACION_PROVINCIAS.put("san miguel de tucuman", "Tucumán");

        // Salta
        NORMALIZACION_PROVINCIAS.put("salta", "Salta");

        // Entre Ríos
        NORMALIZACION_PROVINCIAS.put("entre rios", "Entre Ríos");
        NORMALIZACION_PROVINCIAS.put("entre ríos", "Entre Ríos");

        // Misiones
        NORMALIZACION_PROVINCIAS.put("misiones", "Misiones");

        // Chaco
        NORMALIZACION_PROVINCIAS.put("chaco", "Chaco");

        // Corrientes
        NORMALIZACION_PROVINCIAS.put("corrientes", "Corrientes");

        // Santiago del Estero
        NORMALIZACION_PROVINCIAS.put("santiago del estero", "Santiago del Estero");
        NORMALIZACION_PROVINCIAS.put("sgo del estero", "Santiago del Estero");

        // Jujuy
        NORMALIZACION_PROVINCIAS.put("jujuy", "Jujuy");

        // San Juan
        NORMALIZACION_PROVINCIAS.put("san juan", "San Juan");

        // San Luis
        NORMALIZACION_PROVINCIAS.put("san luis", "San Luis");

        // Catamarca
        NORMALIZACION_PROVINCIAS.put("catamarca", "Catamarca");

        // La Rioja
        NORMALIZACION_PROVINCIAS.put("la rioja", "La Rioja");

        // Formosa
        NORMALIZACION_PROVINCIAS.put("formosa", "Formosa");

        // Neuquén
        NORMALIZACION_PROVINCIAS.put("neuquen", "Neuquén");
        NORMALIZACION_PROVINCIAS.put("neuquén", "Neuquén");

        // Río Negro
        NORMALIZACION_PROVINCIAS.put("rio negro", "Río Negro");
        NORMALIZACION_PROVINCIAS.put("río negro", "Río Negro");

        // Chubut
        NORMALIZACION_PROVINCIAS.put("chubut", "Chubut");

        // Santa Cruz
        NORMALIZACION_PROVINCIAS.put("santa cruz", "Santa Cruz");

        // Tierra del Fuego
        NORMALIZACION_PROVINCIAS.put("tierra del fuego", "Tierra del Fuego");
        NORMALIZACION_PROVINCIAS.put("tierra del fuego antartida e islas del atlantico sur", "Tierra del Fuego");
        NORMALIZACION_PROVINCIAS.put("tdf", "Tierra del Fuego");

        // La Pampa
        NORMALIZACION_PROVINCIAS.put("la pampa", "La Pampa");
        NORMALIZACION_PROVINCIAS.put("pampa", "La Pampa");
    }

    public String normalizarProvincia(String provincia) {
        if (provincia == null || provincia.isBlank()) {
            return "Provincia Desconocida";
        }

        String provinciaLower = provincia.toLowerCase().trim();
        return NORMALIZACION_PROVINCIAS.getOrDefault(provinciaLower, provincia);
    }

    public String obtenerProvinciaPorCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            log.warn("[GeocodificacionService] Coordenadas nulas");
            return "Provincia Desconocida";
        }

        // Ciudad Autónoma de Buenos Aires
        if (latitud >= -34.7 && latitud <= -34.5 && longitud >= -58.55 && longitud <= -58.3) {
            return "Ciudad Autónoma de Buenos Aires";
        }

        // Buenos Aires (provincia)
        if (latitud >= -41.0 && latitud <= -33.5 && longitud >= -63.5 && longitud <= -56.5) {
            return "Buenos Aires";
        }

        // Córdoba
        if (latitud >= -35.0 && latitud <= -29.5 && longitud >= -65.5 && longitud <= -61.5) {
            return "Córdoba";
        }

        // Santa Fe
        if (latitud >= -34.5 && latitud <= -28.0 && longitud >= -62.5 && longitud <= -58.5) {
            return "Santa Fe";
        }

        // Mendoza
        if (latitud >= -37.5 && latitud <= -32.0 && longitud >= -70.5 && longitud <= -66.5) {
            return "Mendoza";
        }

        // Tucumán
        if (latitud >= -28.0 && latitud <= -26.0 && longitud >= -66.0 && longitud <= -64.5) {
            return "Tucumán";
        }

        // Salta
        if (latitud >= -25.5 && latitud <= -22.0 && longitud >= -66.5 && longitud <= -62.0) {
            return "Salta";
        }

        // Entre Ríos
        if (latitud >= -34.0 && latitud <= -30.0 && longitud >= -60.5 && longitud <= -57.5) {
            return "Entre Ríos";
        }

        // Misiones
        if (latitud >= -28.0 && latitud <= -25.5 && longitud >= -56.5 && longitud <= -53.5) {
            return "Misiones";
        }

        // Chaco
        if (latitud >= -28.0 && latitud <= -24.5 && longitud >= -63.0 && longitud <= -58.5) {
            return "Chaco";
        }

        // Corrientes
        if (latitud >= -30.5 && latitud <= -27.0 && longitud >= -59.5 && longitud <= -55.5) {
            return "Corrientes";
        }

        // Santiago del Estero
        if (latitud >= -30.0 && latitud <= -25.5 && longitud >= -65.5 && longitud <= -61.0) {
            return "Santiago del Estero";
        }

        // Jujuy
        if (latitud >= -24.5 && latitud <= -21.5 && longitud >= -67.0 && longitud <= -64.0) {
            return "Jujuy";
        }

        // San Juan
        if (latitud >= -32.5 && latitud <= -28.5 && longitud >= -70.0 && longitud <= -66.5) {
            return "San Juan";
        }

        // San Luis
        if (latitud >= -36.0 && latitud <= -32.0 && longitud >= -67.5 && longitud <= -64.5) {
            return "San Luis";
        }

        // Catamarca
        if (latitud >= -29.5 && latitud <= -25.5 && longitud >= -69.0 && longitud <= -65.0) {
            return "Catamarca";
        }

        // La Rioja
        if (latitud >= -31.5 && latitud <= -28.0 && longitud >= -69.5 && longitud <= -65.5) {
            return "La Rioja";
        }

        // Formosa
        if (latitud >= -26.5 && latitud <= -23.0 && longitud >= -62.5 && longitud <= -57.5) {
            return "Formosa";
        }

        // Neuquén
        if (latitud >= -41.0 && latitud <= -36.5 && longitud >= -71.5 && longitud <= -68.0) {
            return "Neuquén";
        }

        // Río Negro
        if (latitud >= -42.0 && latitud <= -37.5 && longitud >= -71.5 && longitud <= -62.5) {
            return "Río Negro";
        }

        // Chubut
        if (latitud >= -46.5 && latitud <= -42.0 && longitud >= -72.0 && longitud <= -63.5) {
            return "Chubut";
        }

        // Santa Cruz
        if (latitud >= -52.0 && latitud <= -46.0 && longitud >= -73.5 && longitud <= -65.5) {
            return "Santa Cruz";
        }

        // Tierra del Fuego
        if (latitud >= -55.5 && latitud <= -52.5 && longitud >= -69.0 && longitud <= -63.5) {
            return "Tierra del Fuego";
        }

        // La Pampa
        if (latitud >= -39.5 && latitud <= -35.0 && longitud >= -68.5 && longitud <= -63.5) {
            return "La Pampa";
        }

        log.debug("[GeocodificacionService] Coordenadas fuera de Argentina: lat={}, lon={}",
            latitud, longitud);
        return "Fuera de Argentina";
    }
}


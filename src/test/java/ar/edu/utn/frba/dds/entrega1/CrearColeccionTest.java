package ar.edu.utn.frba.dds.entrega1;

import ar.edu.utn.frba.dds.domain.datasource.FuenteEstatica;
import ar.edu.utn.frba.dds.domain.model.coleccion.Coleccion;
import ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia.CriterioCategoria;
import ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia.CriterioTitulo;
import ar.edu.utn.frba.dds.domain.model.hecho.DataHecho;
import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.model.hecho.OrigenHecho;
import ar.edu.utn.frba.dds.domain.model.user.Administrador;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CrearColeccionTest {
    private Administrador administrador = new Administrador();
    private FuenteEstatica fuente = new FuenteEstatica("src/test/java/ar/edu/utn/frba/dds/entrega1/testFiles/desastres_sanitarios_contaminacion_argentina.csv");

    @Test
    @DisplayName("Administrador crea una colección a partir de un archivo CSV")
    void administradorCreaColeccionConCsv() throws Exception {
        CriterioCategoria criterio = new CriterioCategoria("Desastre Tecnológico - Evento sanitario");
        Coleccion nuevaColeccion = administrador.crearColeccion("Desastres Tecnológicos", "Desastres Tecnológicos en Argentina", fuente, criterio);
        Hecho novenoHecho = nuevaColeccion.obtenerHechos().get(8);
        Assertions.assertEquals("Alerta por Casos agrupados de enfermedad en Embarcación, Salta", novenoHecho.getTitulo());
    }

    @Test
    @DisplayName("La colección queda vacía si ningún hecho cumple el criterio")
    void coleccionConCriterioSinCoincidencias() throws Exception {
        CriterioTitulo criterioInexistente = new CriterioTitulo("Criterio que no existe en el archivo");
        Coleccion coleccion = administrador.crearColeccion("Colección vacía", "Ningún hecho cumple", fuente, criterioInexistente);

        Assertions.assertTrue(coleccion.obtenerHechos().isEmpty());
    }

    @Test
    @DisplayName("La colección incluye hechos cuando el criterio de categoría está en minúsculas")
    void coleccionConCriterioEnMinusculas() throws Exception {
        CriterioCategoria criterio = new CriterioCategoria("evento sanitario");

        Coleccion coleccion = administrador.crearColeccion(
            "Eventos sanitarios",
            "Hechos que contienen 'evento sanitario' en su categoría",
            fuente,
            criterio
        );

        Assertions.assertFalse(coleccion.obtenerHechos().isEmpty());
    }

    @Test
    @DisplayName("El segundo hecho con el mismo título pisa al primero")
    void segundoHechoPisaAlPrimeroPorTitulo() throws Exception {
        List<Hecho> hechosOriginales = fuente.importarHechos();

        String tituloDuplicado = "Se reporta Propagación de enfermedad infecciosa en zona de San Martín";

        DataHecho nuevoData = new DataHecho();
        nuevoData.setTitulo(tituloDuplicado);
        nuevoData.setDescripcion("Test");
        nuevoData.setCategoria("Evento sanitario");
        nuevoData.setLatitud(-40.157);
        nuevoData.setLongitud(-71.353);
        nuevoData.setFechaDeAcontecimiento(LocalDate.of(2025, 6, 15));

        Hecho nuevoHecho = new Hecho(nuevoData, OrigenHecho.MANUAL);

        Map<String, Hecho> hechosPorTitulo = new LinkedHashMap<>();
        hechosOriginales.forEach(hecho -> hechosPorTitulo.put(hecho.getTitulo(), hecho));
        hechosPorTitulo.put(nuevoHecho.getTitulo(), nuevoHecho);

        Hecho hechoFinal = hechosPorTitulo.get(tituloDuplicado);
        Assertions.assertEquals("Test", hechoFinal.getDescripcion());
    }

}

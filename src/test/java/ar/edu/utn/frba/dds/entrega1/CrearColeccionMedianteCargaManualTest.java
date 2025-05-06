package ar.edu.utn.frba.dds.entrega1;


import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.Criterio;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.CriterioCategoria;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.CriterioCombinado;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.CriterioFechas;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.CriterioTitulo;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.TipoFecha;
import ar.edu.utn.frba.dds.service.Fuente;
import ar.edu.utn.frba.dds.domain.coleccion.Coleccion;
import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.user.Administrador;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

class CrearColeccionMedianteCargaManualTest {
    private Hecho hecho1;
    private Hecho hecho2;
    private Hecho hecho3;
    private Hecho hecho4;
    private Hecho hecho5;
    private Administrador administrador;
    private Fuente fuente;
    private Coleccion coleccion;

    @BeforeEach
    void init()
    {
        hecho1 = new Hecho("Caída de aeronave impacta en Olavarría",
                "Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
                "Caída de aeronave",
                LocalDate.of(2001, 11, 29),
                -36.868375, -60.343297);
        hecho2 = new Hecho("Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén",
                "Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
                "Accidente con maquinaria industrial",
                LocalDate.of(2001, 8, 16),
                -37.345571, -70.241485);
        hecho3 = new Hecho("Caída de aeronave impacta en Venado Tuerto, Santa Fe",
                "Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.",
                "Caída de aeronave",
                LocalDate.of(2008, 8, 8),
                -33.768051, -61.921032);
        hecho4 = new Hecho("Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires",
                "Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo Social está brindando apoyo a los damnificados.",
                "Accidente en paso a nivel",
                LocalDate.of(2020, 1, 27),
                -35.855811, -61.940589);
        hecho5 = new Hecho("Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña",
                "Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.",
                "Derrumbe en obra en construcción",
                LocalDate.of(2016, 6, 4),
                -26.780008, -60.458782);
        administrador = new Administrador();
        List<Hecho> hechos = List.of(hecho1, hecho2, hecho3, hecho4, hecho5);
        fuente = mock(Fuente.class);
        try {
        when(fuente.importarHechos()).thenReturn(hechos);
        coleccion = administrador.crearColeccion("Colección prueba", "Esto es una prueba", fuente, null);

        } catch (Exception e) {
            fail("No se esperaba una excepción: " + e.getMessage());
        }
    }
    @Test
    @DisplayName("Escenario 1:  Creación de colección mediante carga manual")
    void testCreacionColeccionMedianteCargaManual() throws Exception{
        Hecho segundoHecho = coleccion.obtenerHechos().get(1);
        Assertions.assertEquals(-37.345571, segundoHecho.getLatitud());
    }

    @Test
    @DisplayName("Escenario 1.2: Criterios de pertenencia")
    void testCriterioPertenencia() throws Exception {
        LocalDate fechaInicial = LocalDate.of(2000, 1, 1);
        LocalDate fechaFinal = LocalDate.of(2010, 1, 1);
        Criterio nuevoCriterio = new CriterioFechas(fechaInicial, fechaFinal, TipoFecha.ACONTECIMIENTO);
        coleccion.setCriterioDePertenencia(nuevoCriterio);

        List<Hecho> hechosFiltrados = coleccion.obtenerHechos();

        Assertions.assertEquals(3, hechosFiltrados.size(), "La colección debe contener solo tres hechos.");

        // Verificar que los hechos son los tres primeros que cumplen con la condición de fecha
        Assertions.assertTrue(hechosFiltrados.get(0).getFechaDeAcontecimiento().isBefore(LocalDate.of(2010, 1, 1)), "El primer hecho debe estar antes de 01/01/2010.");
        Assertions.assertTrue(hechosFiltrados.get(1).getFechaDeAcontecimiento().isBefore(LocalDate.of(2010, 1, 1)), "El segundo hecho debe estar antes de 01/01/2010.");
        Assertions.assertTrue(hechosFiltrados.get(2).getFechaDeAcontecimiento().isBefore(LocalDate.of(2010, 1, 1)), "El tercer hecho debe estar antes de 01/01/2010.");
    }

    @Test
    @DisplayName("Escenario 1.2: Doble criterio de pertenencia.")
    void testDobleCriterioPertenencia() throws Exception {
        LocalDate fechaInicial = LocalDate.of(2000, 1, 1);
        LocalDate fechaFinal = LocalDate.of(2010, 1, 1);
        Criterio nuevoCriterio = new CriterioFechas(fechaInicial, fechaFinal, TipoFecha.ACONTECIMIENTO);

        Criterio otroCriterio = new CriterioCategoria("Caída de aeronave");

        List<Criterio> criterios = List.of(nuevoCriterio, otroCriterio);
        Criterio criterioCombinado = new CriterioCombinado(criterios);

        coleccion.setCriterioDePertenencia(criterioCombinado);

        List<Hecho> hechosFiltrados = coleccion.obtenerHechos();

        Assertions.assertEquals(2, hechosFiltrados.size(), "La colección debe contener solo tres hechos.");

        // Verificar que los hechos son los tres primeros que cumplen con la condición de fecha
        Assertions.assertEquals(-36.868375, hechosFiltrados.get(0).getLatitud());
        Assertions.assertEquals(-33.768051, hechosFiltrados.get(1).getLatitud());
    }

    @Test
    @DisplayName("Escenario 1.3: Filtros del visualizador")
    void testFiltroVisualizador() throws Exception{
        Criterio unCriterio = new CriterioCategoria("Caída de Aeronave");
        Criterio otroCriterio = new CriterioTitulo("un título");

        List<Criterio> criterios = List.of(unCriterio, otroCriterio);
        Criterio criterioCombinado = new CriterioCombinado(criterios);

        coleccion.setCriterioDePertenencia(criterioCombinado);

        List<Hecho> hechosFiltrados = coleccion.obtenerHechos();

        Assertions.assertTrue(hechosFiltrados.isEmpty(), "La lista deberia estar vacia");

    }
    @Test
    @DisplayName("Escenario 1.4: Etiquetas")
    void testEtiquetas() throws Exception {
        hecho1.agregarEtiqueta("Olavarría");
        hecho1.agregarEtiqueta("Grave");

        Hecho hechoOlavarria = coleccion.obtenerHechos().stream()
            .filter(h -> h.getTitulo().equals("Caída de aeronave impacta en Olavarría"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el hecho esperado"));

        Assertions.assertTrue(hechoOlavarria.contieneEtiqueta("Olavarría"), "El hecho debe tener la etiqueta 'Olavarría'");
        Assertions.assertTrue(hechoOlavarria.contieneEtiqueta("Grave"), "El hecho debe tener la etiqueta 'Grave'");
    }

}
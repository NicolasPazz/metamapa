package ar.edu.utn.frba.dds.entrega1;

import ar.edu.utn.frba.dds.datasource.FuenteEstatica;
import ar.edu.utn.frba.dds.model.domain.coleccion.Coleccion;
import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.user.Administrador;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class crearColeccionTest {
    private Administrador administrador = new Administrador();
    private FuenteEstatica fuente = new FuenteEstatica("src/test/java/ar/edu/utn/frba/dds/entrega1/testFiles/fires-all.csv");

    @Test
    @DisplayName("Administrador crea una colección a partir de un archivo CSV")
    void administradorCreaColeccionConCsv() throws Exception {
        Coleccion nuevaColeccion = this.administrador.crearColeccion("Incendios en España", "Recopilación de incendios en España", this.fuente, "Incendios");
        Hecho novenoHecho = nuevaColeccion.obtenerHechos().get(8);
        Assertions.assertEquals("1968440009", novenoHecho.getData().getTitulo());
    }
}

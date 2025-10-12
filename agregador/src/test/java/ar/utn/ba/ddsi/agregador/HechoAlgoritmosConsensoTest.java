package ar.utn.ba.ddsi.agregador;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.absoluta;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.mayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.multiplesMenciones;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HechoAlgoritmosConsensoTest {

    private Hecho hecho;

    @BeforeEach
    void setUp() {
        hecho = new Hecho();
    }

    @Test
    public void agregarAlgoritmosSinDuplicados() {
        AAlgoritmoConsenso abs = new absoluta();
        AAlgoritmoConsenso ms = new mayoriaSimple();
        AAlgoritmoConsenso mm = new multiplesMenciones();

        hecho.agregarAlgoritmoConsenso(abs);
        hecho.agregarAlgoritmoConsenso(ms);
        hecho.agregarAlgoritmoConsenso(mm);
        hecho.agregarAlgoritmoConsenso(abs); // no debe duplicar

        List<String> codigos = hecho.getAlgoritmosConsenso();
        assertNotNull(codigos);
        assertEquals(3, codigos.size());
        assertTrue(codigos.contains("absoluta"));
        assertTrue(codigos.contains("mayoriaSimple"));
        assertTrue(codigos.contains("multiplesMenciones"));
    }

    @Test
    public void roundTripAInstancias() {
        hecho.agregarAlgoritmoConsenso(new absoluta());
        hecho.agregarAlgoritmoConsenso(new mayoriaSimple());
        hecho.agregarAlgoritmoConsenso(new multiplesMenciones());

        List<AAlgoritmoConsenso> instancias = hecho.obtenerAlgoritmosConsenso();
        assertNotNull(instancias);
        assertEquals(3, instancias.size());
        assertTrue(instancias.stream().anyMatch(a -> a instanceof absoluta));
        assertTrue(instancias.stream().anyMatch(a -> a instanceof mayoriaSimple));
        assertTrue(instancias.stream().anyMatch(a -> a instanceof multiplesMenciones));
    }

    @Test
    public void setDesdeInstanciasPersisteCodigos() {
        List<AAlgoritmoConsenso> instancias = List.of(
                new absoluta(), new mayoriaSimple(), new multiplesMenciones()
        );

        Hecho otro = new Hecho();
        otro.setAlgoritmosConsensoDesdeInstancias(instancias);

        List<String> codigos = otro.getAlgoritmosConsenso();
        assertNotNull(codigos);
        assertEquals(3, codigos.size());
        assertTrue(codigos.containsAll(List.of("absoluta", "mayoriaSimple", "multiplesMenciones")));
    }

    @Test
    public void removerAlgoritmo() {
        hecho.setAlgoritmosConsensoDesdeInstancias(List.of(
                new absoluta(), new mayoriaSimple(), new multiplesMenciones()
        ));

        hecho.removerAlgoritmoConsenso(new mayoriaSimple());

        List<String> codigos = hecho.getAlgoritmosConsenso();
        assertEquals(2, codigos.size());
        assertFalse(codigos.contains("mayoriaSimple"));
        assertTrue(codigos.contains("absoluta"));
        assertTrue(codigos.contains("multiplesMenciones"));
    }
}
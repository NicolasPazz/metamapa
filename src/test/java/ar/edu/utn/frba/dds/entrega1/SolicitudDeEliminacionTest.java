package ar.edu.utn.frba.dds.entrega1;

import ar.edu.utn.frba.dds.service.impl.FuenteDinamica;
import ar.edu.utn.frba.dds.domain.coleccion.Coleccion;
import ar.edu.utn.frba.dds.domain.hecho.DataHecho;
import ar.edu.utn.frba.dds.domain.hecho.EstadoHecho;
import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.hecho.OrigenHecho;
import ar.edu.utn.frba.dds.domain.request.EstadoSolicitud;
import ar.edu.utn.frba.dds.domain.request.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.domain.user.Administrador;
import ar.edu.utn.frba.dds.domain.user.Contribuyente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class SolicitudDeEliminacionTest {
  private Hecho hecho;
  private Contribuyente contribuyente;
  private Administrador administrador;
  private String textoLargo;
  private String textoCorto;

  @BeforeEach
  void init() {
    String titulo = "Brote de enfermedad contagiosa causa estragos en San Lorenzo, Santa Fe";
    String descripcion = "Grave brote de enfermedad contagiosa ocurrió en las inmediaciones de San Lorenzo, Santa Fe. El incidente dejó varios heridos y daños materiales. Se ha declarado estado de emergencia en la región para facilitar la asistencia.";
    DataHecho dataHecho = new DataHecho();
    dataHecho.setDescripcion(descripcion);
    dataHecho.setTitulo(titulo);
    dataHecho.setCategoria("Evento sanitario");
    dataHecho.setLatitud(-32.786098);
    dataHecho.setLongitud(-60.741543);
    dataHecho.setFechaDeAcontecimiento(LocalDate.of(2005, 7, 5));

    hecho = new Hecho(dataHecho, OrigenHecho.MANUAL);
    contribuyente = new Contribuyente();
    administrador = new Administrador();
    textoLargo = "a".repeat(501); // Tiene mas de 500 caracteres
    textoCorto = "a";
  }

  @Test
  @DisplayName("Un contribuyente puede crear una solicitud de eliminacion y esta quedara en estado pendiente")
  void contribuyentePuedeCrearSolicitudDeEliminacion() {
    SolicitudDeEliminacion solicitud = contribuyente.solicitarEliminacion(hecho, textoLargo);
    Assertions.assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
  }

  @Test
  @DisplayName("Un administrador puede rechazar una solicitud de eliminacion y esta quedara en estado rechazada")
  void administradorRechazaSolicitud() {
    SolicitudDeEliminacion solicitud = contribuyente.solicitarEliminacion(hecho, textoLargo);
    solicitud.setFecha(solicitud.getFecha().minusDays(1));
    Assertions.assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    administrador.rechazarSolicitud(solicitud);
    Assertions.assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
  }

  @Test
  @DisplayName("Un administrador puede aceptar una solicitud de eliminacion, esta quedara en estado aceptada y no permite que el hecho se agregue a una coleccion")
  void administradorAceptaSolicitud() throws Exception {
    SolicitudDeEliminacion solicitud = contribuyente.solicitarEliminacion(hecho, textoLargo);
    administrador.aceptarSolicitud(solicitud);

    FuenteDinamica fuente = new FuenteDinamica(List.of(hecho));
    Coleccion coleccion = new Coleccion("Coleccion de prueba", "Test para hechos ocultos", fuente, null);
    List<Hecho> hechosVisibles = coleccion.obtenerHechos();

    Assertions.assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado());
    Assertions.assertEquals(EstadoHecho.OCULTO, hecho.getEstado());
    Assertions.assertFalse(hechosVisibles.contains(hecho));
  }

  @Test
  @DisplayName("Una solicitud de eliminacion no puede ser creada si el texto de fundamentos tiene menos de 500 caracteres")
  void solicitudConFundamentosCortos() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      contribuyente.solicitarEliminacion(hecho, textoCorto);
    });
  }

  @Test
  @DisplayName("Una solicitud con exactamente 500 caracteres también es válida")
  void solicitudConTextoExacto500() {
    String texto500 = "a".repeat(500);
    SolicitudDeEliminacion solicitud = contribuyente.solicitarEliminacion(hecho, texto500);
    Assertions.assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
  }
}

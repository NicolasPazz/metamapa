package ar.edu.utn.frba.dds.entrega1;

import ar.edu.utn.frba.dds.model.domain.hecho.EstadoHecho;
import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.hecho.OrigenHecho;
import ar.edu.utn.frba.dds.model.domain.request.EstadoSolicitud;
import ar.edu.utn.frba.dds.model.domain.request.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.model.domain.user.Administrador;
import ar.edu.utn.frba.dds.model.domain.user.Contribuyente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

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

    hecho = new Hecho(titulo, descripcion, "Evento sanitario", -32.786098, -60.741543, LocalDateTime.of(2005, 7, 5, 0, 0), OrigenHecho.MANUAL);
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
    administrador.rechazarSolicitud(solicitud);
    Assertions.assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
  }

  @Test
  @DisplayName("Un administrador puede aceptar una solicitud de eliminacion y esta quedara en estado aceptada")
  void administradorAceptaSolicitud() {
    SolicitudDeEliminacion solicitud = contribuyente.solicitarEliminacion(hecho, textoLargo);
    administrador.aceptarSolicitud(solicitud);
    Assertions.assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado());
    Assertions.assertEquals(EstadoHecho.OCULTO, hecho.getEstado());
  }

  @Test
  @DisplayName("Una solicitud de eliminacion no puede ser creada si el texto de fundamentos tiene menos de 500 caracteres")
  void solicitudConFundamentosCortos() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      contribuyente.solicitarEliminacion(hecho, textoCorto);
    });
  }
}

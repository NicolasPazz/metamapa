package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.TransicionEstadoHechoDTO;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"dev"})
@Getter
public class StubDataFixture {

  private final List<HechoDTO> hechos = new ArrayList<>();
  private final List<ColeccionDTO> colecciones = new ArrayList<>();

  public StubDataFixture() {
    cargarHechos();
    cargarColecciones();
  }

  private void cargarHechos() {

    ContenidoMultimediaDTO imgIncendio =
        new ContenidoMultimediaDTO("/stub/img/incendio-1.jpg", "IMAGEN");
    TransicionEstadoHechoDTO transIncendio =
        new TransicionEstadoHechoDTO("OCULTO", "2025-03-21T15:00:00");

    HechoDTO incendioCordoba = new HechoDTO(
        /* contribuyente */             6775L,
        /* etiquetas      */            List.of("incendio", "córdoba"),
        /* titulo         */            "Incendio forestal en Córdoba",
        /* descripcion    */            "Fuego controlado luego de 3 h.",
        /* categoria      */            "Incendio forestal",
        /* latitud        */            -31.42,
        /* longitud       */            -64.19,
        /* fecha acontec. */            LocalDate.of(2025, 3, 21),
        /* fecha carga    */            LocalDateTime.of(2025, 3, 21, 14, 30),
        /* última modif.  */            LocalDateTime.of(2025, 3, 21, 14, 31),
        /* multimedia     */            List.of(imgIncendio),
        /* transiciones   */            List.of(transIncendio)
    );
    incendioCordoba.setId(2L);

    ContenidoMultimediaDTO imgInundacion =
        new ContenidoMultimediaDTO("/stub/vid/inundacion-1.mp4", "VIDEO");
    TransicionEstadoHechoDTO transInundacion =
        new TransicionEstadoHechoDTO("VISIBLE", "2025-04-02T17:40:00");

    HechoDTO inundacionLaPlata = new HechoDTO(
        1002L,
        List.of("inundación", "la plata"),
        "Inundación en La Plata",
        "Evacuados y cortes de luz.",
        "Inundación",
        -34.92,
        -57.95,
        LocalDate.of(2025, 4, 2),
        LocalDateTime.of(2025, 4, 2, 16, 0),
        LocalDateTime.of(2025, 4, 2, 16, 10),
        List.of(imgInundacion),
        List.of(transIncendio, transInundacion)
    );
    inundacionLaPlata.setId(1L);
    hechos.addAll(List.of(incendioCordoba, inundacionLaPlata));
  }

  private void cargarColecciones() {
    ColeccionDTO desastres2025 = new ColeccionDTO(
        "Desastres naturales AR 2025",
        "Eventos reportados en 2025",
        hechos
    );

    colecciones.add(desastres2025);
  }
}
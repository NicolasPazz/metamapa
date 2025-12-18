package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.enums.EstadoHecho;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    TransicionEstadoHechoDTO transVisible = new TransicionEstadoHechoDTO(
        EstadoHecho.VISIBLE,
        LocalDateTime.of(2025, 1, 1, 10, 0, 0)
    );

    // Hecho 1: Incendio Córdoba (Anónimo)
    HechoDTO incendioCordoba = new HechoDTO(
        null,
        List.of("incendio", "córdoba", "forestal"),
        "Incendio forestal en Córdoba",
        "Fuego controlado luego de 3 horas de trabajo intenso de bomberos.",
        "Incendio forestal",
        -31.42,
        -64.19,
        LocalDateTime.of(2025, 3, 21, 12, 0, 0),
        LocalDateTime.of(2025, 3, 21, 14, 30),
        List.of(new ContenidoMultimediaDTO("incendio-cordoba.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    incendioCordoba.setId(1L);
    incendioCordoba.setEstado(EstadoHecho.VISIBLE);

    // Hecho 2: Inundación La Plata (Anónimo)
    HechoDTO inundacionLaPlata = new HechoDTO(
        null,
        List.of("inundación", "la plata", "evacuados"),
        "Inundación en La Plata",
        "Evacuados y cortes de luz debido a intensas lluvias.",
        "Inundación",
        -34.92,
        -57.95,
        LocalDateTime.of(2025, 4, 2, 8, 30, 0),
        LocalDateTime.of(2025, 4, 2, 16, 0),
        List.of(new ContenidoMultimediaDTO("inundacion-laplata.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    inundacionLaPlata.setId(2L);
    inundacionLaPlata.setEstado(EstadoHecho.VISIBLE);

    // Hecho 3: Terremoto Mendoza (Anónimo)
    HechoDTO terremotoMendoza = new HechoDTO(
        null,
        List.of("terremoto", "mendoza", "sismo"),
        "Terremoto de 4.2 grados en Mendoza",
        "Sismo de mediana intensidad sin víctimas reportadas.",
        "Terremoto",
        -32.89,
        -68.83,
        LocalDateTime.of(2025, 5, 15, 8, 45, 0),
        LocalDateTime.of(2025, 5, 15, 8, 45),
        List.of(new ContenidoMultimediaDTO("terremoto-mendoza.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    terremotoMendoza.setId(3L);
    terremotoMendoza.setEstado(EstadoHecho.VISIBLE);

    // Hecho 4: Tornado Santa Fe (Anónimo)
    HechoDTO tornadoSantaFe = new HechoDTO(
        null,
        List.of("tornado", "santa fe", "vientos"),
        "Tornado en zona rural de Santa Fe",
        "Tornado F2 causa daños materiales en campos agrícolas.",
        "Tornado",
        -31.62,
        -60.69,
        LocalDateTime.of(2025, 6, 10, 17, 20, 0),
        LocalDateTime.of(2025, 6, 10, 17, 20),
        List.of(new ContenidoMultimediaDTO("tornado-santafe.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    tornadoSantaFe.setId(4L);
    tornadoSantaFe.setEstado(EstadoHecho.VISIBLE);

    // Hecho 5: Contaminación Riachuelo (Anónimo)
    HechoDTO contaminacionRiachuelo = new HechoDTO(
        null,
        List.of("contaminación", "riachuelo", "ambiental"),
        "Derrame tóxico en el Riachuelo",
        "Derrame de sustancias químicas detectado en la cuenca del Riachuelo.",
        "Contaminación hídrica",
        -34.64,
        -58.37,
        LocalDateTime.of(2025, 7, 5, 11, 30, 0),
        LocalDateTime.of(2025, 7, 5, 11, 30),
        List.of(new ContenidoMultimediaDTO("contaminacion-riachuelo.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    contaminacionRiachuelo.setId(5L);
    contaminacionRiachuelo.setEstado(EstadoHecho.VISIBLE);

    // Hecho 6: Granizo Rosario (Anónimo)
    HechoDTO granizoRosario = new HechoDTO(
        null,
        List.of("granizo", "rosario", "tormenta"),
        "Intensa tormenta de granizo en Rosario",
        "Granizo del tamaño de pelotas de golf causa daños en autos y techos.",
        "Granizo",
        -32.95,
        -60.64,
        LocalDateTime.of(2025, 8, 20, 19, 15, 0),
        LocalDateTime.of(2025, 8, 20, 19, 15),
        List.of(new ContenidoMultimediaDTO("granizo-rosario.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    granizoRosario.setId(6L);
    granizoRosario.setEstado(EstadoHecho.VISIBLE);

    // Hecho 7: Sequía Salta (Anónimo)
    HechoDTO sequiaSalta = new HechoDTO(
        null,
        List.of("sequía", "salta", "agricultura"),
        "Sequía severa afecta cultivos en Salta",
        "Tres meses sin lluvias generan pérdidas en la producción agrícola.",
        "Sequía",
        -24.78,
        -65.41,
        LocalDateTime.of(2025, 9, 1, 10, 0, 0),
        LocalDateTime.of(2025, 9, 1, 10, 0),
        List.of(new ContenidoMultimediaDTO("sequia-salta.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    sequiaSalta.setId(7L);
    sequiaSalta.setEstado(EstadoHecho.VISIBLE);

    // Hecho 8: Deslizamiento Tucumán (Anónimo)
    HechoDTO deslizamientoTucuman = new HechoDTO(
        null,
        List.of("deslizamiento", "tucumán", "tierra"),
        "Deslizamiento de tierra en ruta tucumana",
        "Lluvias intensas provocan deslizamiento que bloquea ruta provincial.",
        "Deslizamiento",
        -26.82,
        -65.22,
        LocalDateTime.of(2025, 10, 12, 14, 0, 0),
        LocalDateTime.of(2025, 10, 12, 14, 0),
        List.of(new ContenidoMultimediaDTO("deslizamiento-tucuman.jpg", "IMAGEN")),
        List.of(transVisible)
    );
    deslizamientoTucuman.setId(8L);
    deslizamientoTucuman.setEstado(EstadoHecho.VISIBLE);

    hechos.addAll(List.of(
        incendioCordoba,
        inundacionLaPlata,
        terremotoMendoza,
        tornadoSantaFe,
        contaminacionRiachuelo,
        granizoRosario,
        sequiaSalta,
        deslizamientoTucuman
    ));
  }

  private void cargarColecciones() {
    // Colección 1: Todos los desastres
    ColeccionDTO todosDesastres = ColeccionDTO.builder()
        .handle(UUID.fromString("11111111-1111-1111-1111-111111111111"))
        .titulo("Desastres Naturales Argentina 2025")
        .descripcion("Compilación de todos los eventos climáticos y geológicos reportados en Argentina durante 2025")
        .fechaDeCarga(LocalDateTime.of(2025, 1, 1, 0, 0))
        .hechos(new ArrayList<>(hechos))
        .hechosConsensuados(new ArrayList<>(hechos))
        .build();

    // Colección 2: Eventos climáticos
    List<HechoDTO> eventosClimaticos = hechos.stream()
        .filter(h -> h.getCategoria().contains("Inundación") ||
                     h.getCategoria().contains("Granizo") ||
                     h.getCategoria().contains("Tornado") ||
                     h.getCategoria().contains("Sequía"))
        .toList();

    ColeccionDTO climaticos = ColeccionDTO.builder()
        .handle(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        .titulo("Eventos Climáticos Extremos")
        .descripcion("Fenómenos meteorológicos severos: inundaciones, tornados, granizo y sequías")
        .fechaDeCarga(LocalDateTime.of(2025, 1, 15, 0, 0))
        .hechos(new ArrayList<>(eventosClimaticos))
        .hechosConsensuados(new ArrayList<>(eventosClimaticos))
        .build();

    // Colección 3: Incendios forestales
    List<HechoDTO> incendios = hechos.stream()
        .filter(h -> h.getCategoria().contains("Incendio"))
        .toList();

    ColeccionDTO forestales = ColeccionDTO.builder()
        .handle(UUID.fromString("33333333-3333-3333-3333-333333333333"))
        .titulo("Incendios Forestales")
        .descripcion("Registro de incendios en zonas forestales y rurales")
        .fechaDeCarga(LocalDateTime.of(2025, 2, 1, 0, 0))
        .hechos(new ArrayList<>(incendios))
        .hechosConsensuados(new ArrayList<>(incendios))
        .build();

    // Colección 4: Eventos geológicos
    List<HechoDTO> geologicos = hechos.stream()
        .filter(h -> h.getCategoria().contains("Terremoto") ||
                     h.getCategoria().contains("Deslizamiento"))
        .toList();

    ColeccionDTO sismicos = ColeccionDTO.builder()
        .handle(UUID.fromString("44444444-4444-4444-4444-444444444444"))
        .titulo("Eventos Geológicos")
        .descripcion("Terremotos, sismos y deslizamientos de tierra en territorio argentino")
        .fechaDeCarga(LocalDateTime.of(2025, 3, 1, 0, 0))
        .hechos(new ArrayList<>(geologicos))
        .hechosConsensuados(new ArrayList<>(geologicos))
        .build();

    // Colección 5: Contaminación ambiental
    List<HechoDTO> contaminacion = hechos.stream()
        .filter(h -> h.getCategoria().contains("Contaminación"))
        .toList();

    ColeccionDTO ambiental = ColeccionDTO.builder()
        .handle(UUID.fromString("55555555-5555-5555-5555-555555555555"))
        .titulo("Alertas de Contaminación")
        .descripcion("Incidentes de contaminación hídrica y ambiental")
        .fechaDeCarga(LocalDateTime.of(2025, 4, 1, 0, 0))
        .hechos(new ArrayList<>(contaminacion))
        .hechosConsensuados(new ArrayList<>(contaminacion))
        .build();

    // Colección 6: Eventos del primer semestre
    List<HechoDTO> primerSemestre = hechos.stream()
        .filter(h -> h.getFechaDeAcontecimiento().getMonthValue() <= 6)
        .toList();

    ColeccionDTO semestre1 = ColeccionDTO.builder()
        .handle(UUID.fromString("66666666-6666-6666-6666-666666666666"))
        .titulo("Primer Semestre 2025")
        .descripcion("Eventos ocurridos entre enero y junio de 2025")
        .fechaDeCarga(LocalDateTime.of(2025, 7, 1, 0, 0))
        .hechos(new ArrayList<>(primerSemestre))
        .hechosConsensuados(new ArrayList<>(primerSemestre))
        .build();

    colecciones.addAll(List.of(
        todosDesastres,
        climaticos,
        forestales,
        sismicos,
        ambiental,
        semestre1
    ));
  }
}
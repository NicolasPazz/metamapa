package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.client.AgregadorClient;
import ar.utn.ba.ddsi.estadistica.domain.hecho.Categoria;
import ar.utn.ba.ddsi.estadistica.domain.Coleccion;
import ar.utn.ba.ddsi.estadistica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estadistica.domain.hecho.HechosCategoriaHora;
import ar.utn.ba.ddsi.estadistica.domain.hecho.HechosCategoriaProvincia;
import ar.utn.ba.ddsi.estadistica.domain.hecho.HechosColeccionProvincia;
import ar.utn.ba.ddsi.estadistica.domain.provincia.Provincia;
import ar.utn.ba.ddsi.estadistica.domain.provincia.ProvinciaEnum;
import ar.utn.ba.ddsi.estadistica.domain.dto.input.ColeccionDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.input.HechoDTO;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.ICategoriaRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IColeccionRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosCategoriaHoraRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosCategoriaProvinciaRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosColeccionProvinciaRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IProvinciaRepository;
import ar.utn.ba.ddsi.estadistica.service.IColeccionService;
import ar.utn.ba.ddsi.estadistica.service.IGeolocalizacion;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColeccionService implements IColeccionService {

  private final AgregadorClient agregadorClient;
  private final IGeolocalizacion geolocalizacion;
  private final ICategoriaRepository categoriaRepository;
  private final IColeccionRepository coleccionRepository;
  private final IHechosCategoriaHoraRepository hechosCategoriaHoraRepository;
  private final IHechosCategoriaProvinciaRepository hechosCategoriaProvinciaRepository;
  private final IHechosColeccionProvinciaRepository hechosColeccionProvinciaRepository;
  private final IProvinciaRepository provinciaRepository;

  @Override
  @Transactional
  public List<Coleccion> refrescarColecciones() {
    List<ColeccionDTO> dtos;
    try {
      log.info("Obteniendo todas las colecciones desde el agregador…");
      dtos = agregadorClient.getAllColecciones();
    } catch (Exception e) {
      log.error("No se pudieron obtener colecciones del agregador", e);
      return List.of();
    }
    if (dtos == null || dtos.isEmpty()) {
      log.info("El agregador no devolvió colecciones.");
      return List.of();
    }

    var colecciones = new ArrayList<Coleccion>(dtos.size());
    for (var dto : dtos) {
      colecciones.add(mapearColeccion(dto));
    }
    log.info("Mapeadas {} colecciones.", colecciones.size());

    // limpiar tablas
    hechosColeccionProvinciaRepository.deleteAllInBatch();
    hechosCategoriaProvinciaRepository.deleteAllInBatch();
    hechosCategoriaHoraRepository.deleteAllInBatch();

    coleccionRepository.deleteAllInBatch();
    categoriaRepository.deleteAllInBatch();

    // persistir provincias
    for (var p : ProvinciaEnum.values()) {
      if (p == ProvinciaEnum.INVALIDA) continue;
      provinciaRepository.findByNombre(p)
          .orElseGet(() -> provinciaRepository.save(new Provincia(p)));
    }
    var provinciaByEnum = provinciaRepository.findAll().stream()
        .collect(Collectors.toMap(Provincia::getNombre, Function.identity()));

    // persistir colecciones
    coleccionRepository.saveAll(colecciones);
    var coleccionByHandle = colecciones.stream()
        .collect(Collectors.toMap(Coleccion::getHandle, Function.identity()));

    // persistir categorias
    var nombresCategorias = colecciones.stream()
        .flatMap(c -> c.getHechos().stream())
        .map(Hecho::getCategoria)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    var categorias = nombresCategorias.stream().map(n -> {
      var cat = new Categoria();
      cat.setNombre(n);
      return cat;
    }).toList();
    categoriaRepository.saveAll(categorias);
    var categoriaByNombre = categorias.stream()
        .collect(Collectors.toMap(Categoria::getNombre, Function.identity()));

    // persistir coleccion_provincia
    Map<String, Map<ProvinciaEnum, Long>> cpCounts = colecciones.stream()
        .collect(Collectors.toMap(
            Coleccion::getHandle,
            c -> c.getHechos().stream()
                .filter(h -> h.getProvincia() != null && h.getProvincia() != ProvinciaEnum.INVALIDA)
                .collect(Collectors.groupingBy(Hecho::getProvincia, Collectors.counting()))
        ));

    List<HechosColeccionProvincia> hcpList = new ArrayList<>();
    cpCounts.forEach((handle, mapaProv) -> {
      var cole = coleccionByHandle.get(handle);
      mapaProv.forEach((provEnum, cant) -> {
        var hcp = new HechosColeccionProvincia();
        hcp.setColeccion(cole);
        hcp.setProvincia(provinciaByEnum.get(provEnum));
        hcp.setCantidadHechos(cant);
        hcpList.add(hcp);
      });
    });
    hechosColeccionProvinciaRepository.saveAll(hcpList);

    // persistir categoria_provincia
    Map<String, Map<ProvinciaEnum, Long>> cprCounts = colecciones.stream()
        .flatMap(c -> c.getHechos().stream())
        .filter(h -> h.getCategoria() != null && h.getProvincia() != ProvinciaEnum.INVALIDA)
        .collect(Collectors.groupingBy(
            Hecho::getCategoria,
            Collectors.groupingBy(Hecho::getProvincia, Collectors.counting())
        ));

    List<HechosCategoriaProvincia> hcpvList = new ArrayList<>();
    cprCounts.forEach((catNombre, mapaProv) -> {
      var cat = categoriaByNombre.get(catNombre);
      mapaProv.forEach((provEnum, cant) -> {
        var e = new HechosCategoriaProvincia();
        e.setCategoria(cat);
        e.setProvincia(provinciaByEnum.get(provEnum));
        e.setCantidadHechos(cant);
        hcpvList.add(e);
      });
    });
    hechosCategoriaProvinciaRepository.saveAll(hcpvList);

    // persistir categoria_hora
    Map<String, Map<Integer, Long>> chCounts = colecciones.stream()
        .flatMap(c -> c.getHechos().stream())
        .filter(h -> h.getCategoria() != null && h.getFechaDeCarga() != null)
        .collect(Collectors.groupingBy(
            Hecho::getCategoria,
            Collectors.groupingBy(h -> h.getFechaDeCarga().getHour(), Collectors.counting())
        ));

    List<HechosCategoriaHora> chList = new ArrayList<>();
    chCounts.forEach((catNombre, mapaHora) -> {
      var cat = categoriaByNombre.get(catNombre);
      mapaHora.forEach((hora, cant) -> {
        var e = new HechosCategoriaHora();
        e.setCategoria(cat);
        e.setHora(hora);
        e.setCantidadHechos(cant);
        chList.add(e);
      });
    });
    hechosCategoriaHoraRepository.saveAll(chList);

    return colecciones;
  }

  private Coleccion mapearColeccion(ColeccionDTO dto) {
    var c = new Coleccion();
    c.setHandle(dto.getHandle());
    c.setTitulo(dto.getTitulo());

    if (dto.getHechos() == null || dto.getHechos().isEmpty()) {
      c.setHechos(new ArrayList<>());
    } else {
      c.setHechos(new ArrayList<>(dto.getHechos().stream()
          .map(this::mapearHecho)
          .toList()));
      log.debug("Colección {}: {} hechos mapeados", dto.getHandle(), c.getHechos().size());
    }
    return c;
  }

  private Hecho mapearHecho(HechoDTO hechoDTO) {
    var h = new Hecho();
    h.setCategoria(hechoDTO.getCategoria());
    h.setFechaDeCarga(hechoDTO.getFechaDeCarga());

    var lat = hechoDTO.getLatitud();
    var lng = hechoDTO.getLongitud();
    h.setProvincia(
        (lat != null && lng != null)
            ? geolocalizacion.obtenerProvincia(lat, lng)
            : ProvinciaEnum.INVALIDA
    );
    return h;
  }
}
package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.HechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.HechoModificarInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.*;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ICategoriasRepository;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.user.Contribuyente;
import ar.utn.ba.ddsi.dinamica.service.IHechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HechosService implements IHechosService {

  @Autowired
  private IHechosRepository hechosRepository; //los hechos los voy a sacar de un repositorio

  @Autowired
  private IContribuyentesRepository contribuyentesRepository;

  @Autowired
  private ICategoriasRepository categoriasRepository;

  @Override
  public List<HechoOutputDTO> buscarTodos() {
    // TODO: asegurarme de que el usuario que haga esta peticion tenga permisos(tener en cuenta para modificar un hecho con users logueados)
    List<Hecho> hechos = this.hechosRepository.findAll();
    return hechos
            .stream()
            .map(this::hechoOutputDTO)
            .toList();
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho){
    var dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setOrigen(hecho.getOrigen().name().toLowerCase());
    dto.setEstado(hecho.getEstado().name().toLowerCase());
    dto.setIdContribuyente(
            hecho.getContribuyente() != null ? hecho.getContribuyente().getId() : null
    );
    dto.setIdEtiquetas(hecho.getIdEtiquetas());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setIdCategoria(hecho.getCategoria().getId());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().toString());
    dto.setFechaDeCarga(hecho.getFechaDeCarga().toString());
    dto.setMultimediaPaths(hecho.getMultimediaPaths());
    return dto;
  }


  @Override
  public HechoOutputDTO crearPorContribuyente(HechoInputContribuyenteDTO input) {
    Hecho nuevoHecho = new Hecho();

    DataHecho data = new DataHecho();
    data.setTitulo(input.getTitulo());
    data.setDescripcion(input.getDescripcion());
    data.setNombreCategoria(input.getCategoria());
    data.setLatitud(input.getLatitud());
    data.setLongitud(input.getLongitud());
    data.setFechaDeAcontecimiento(LocalDate.parse(input.getFechaDeAcontecimiento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    nuevoHecho.setData(data);
    nuevoHecho.setOrigen(OrigenHecho.CONTRIBUYENTE);
    nuevoHecho.setEstado(EstadoHecho.VISIBLE);

    Contribuyente contribuyente = this.contribuyentesRepository.findById(input.getIdContribuyente());
    nuevoHecho.setContribuyente(contribuyente);
    //nuevoHecho.setMultimedia(input.getMultimedia());

    Categoria categoria = new Categoria(input.getCategoria());
    this.categoriasRepository.save(categoria);

    nuevoHecho.setCategoria(categoria);

    this.hechosRepository.save(nuevoHecho);

    return this.hechoOutputDTO(nuevoHecho);
  }

  @Override
  public HechoOutputDTO modificarHecho(Long id, HechoModificarInputDTO input) {
    Hecho hecho = hechosRepository.findById(id);
    if (hecho == null) {
      //TODO: throw new IllegalArgumentException("Hecho no encontrado.");
    }

    if (hecho.getContribuyente() == null || !hecho.getContribuyente().getId().equals(input.getIdContribuyente())) {
      //TODO: throw new IllegalArgumentException("El contribuyente no tiene permisos para modificar este hecho.");
    }

    if (ChronoUnit.DAYS.between(hecho.getData().getFechaDeCarga(), LocalDateTime.now()) > 7) {
      //TODO: throw new IllegalArgumentException("El plazo para modificar el hecho ha expirado.");
    }

    // Actualización de campos solo si no son nulos
    if (input.getNuevoTitulo() != null) {
      hecho.getData().setTitulo(input.getNuevoTitulo());
    }

    if (input.getNuevoDescripcion() != null) {
      hecho.getData().setDescripcion(input.getNuevoDescripcion());
    }

    if (input.getNuevoNombreCategoria() != null) {
      Categoria nuevaCategoria = categoriasRepository.findByNombre(input.getNuevoNombreCategoria());
      if (nuevaCategoria == null) {
        nuevaCategoria = new Categoria(input.getNuevoNombreCategoria());
        categoriasRepository.save(nuevaCategoria);
      }
      hecho.getData().setNombreCategoria(nuevaCategoria.getNombre());
      hecho.setCategoria(nuevaCategoria);
    }

    if (input.getNuevoLatitud() != 0.0) {
      hecho.getData().setLatitud(input.getNuevoLatitud());
    }

    if (input.getNuevoLongitud() != 0.0) {
      hecho.getData().setLongitud(input.getNuevoLongitud());
    }

    if (input.getNuevoFechaDeAcontecimiento() != null) {
      LocalDate nuevaFecha = LocalDate.parse(input.getNuevoFechaDeAcontecimiento(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      hecho.getData().setFechaDeAcontecimiento(nuevaFecha);
    }

    if (input.getNuevoContenidoMultimedia() != null) {
      List<ContenidoMultimedia> contenidos = input.getNuevoContenidoMultimedia().stream()
              .map(url -> new ContenidoMultimedia(url, TipoContenido.IMAGEN)) // o ajustar tipo según lógica
              .toList();
      hecho.setMultimedia(contenidos);
    }

    // Persistir cambios (opcional si estás usando objetos en memoria)
    hechosRepository.update(hecho);

    return hechoOutputDTO(hecho);
  }

}

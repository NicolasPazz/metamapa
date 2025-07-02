package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioFuente;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ActualizarColeccionInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ActualizarFuentesColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ModificarCriterioColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.CrearColeccionInputDTO;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import ar.utn.ba.ddsi.agregador.factory.AAlgoritmoDeConsensoFactory;
import ar.utn.ba.ddsi.agregador.factory.CriterioDePertenenciaFactory;
import ar.utn.ba.ddsi.agregador.service.IColeccionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionesService implements IColeccionesService {

    private final IColeccionesRepository coleccionesRepository;
    private final FuentesService fuentesService;
    //private AAlgoritmoConsenso algoritmoConsenso;
    private final AAlgoritmoDeConsensoFactory algoritmoConsensoFactory;
    private final CriterioDePertenenciaFactory criterioDePertenenciaFactory;


    @Override
    public ResponseEntity<List<ColeccionDTO>> getAll() {
      List<ColeccionDTO> locales = coleccionesRepository.findAll().stream()
          .map(Coleccion::mapToDTO)
          .toList();

      List<ColeccionDTO> externas = Optional.ofNullable(
          fuentesService.getAllColecciones().getBody()
      ).orElse(List.of());

      List<ColeccionDTO> combinadas = new ArrayList<>();
      combinadas.addAll(locales);
      combinadas.addAll(externas);

      return ResponseEntity.of(Optional.of(combinadas));
    }

  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      String modo) {
    // Primero, buscar en local
    Coleccion local = coleccionesRepository.findByHandle(handle);

    if (local != null) {
      // Si el modo es CURADA, se obtienen los hechos consensuados
      List<Hecho> hechos = modo.equalsIgnoreCase(ModoNavegacion.CURADA.name()) ? local.getHechosConsensuados() : local.getHechos();

      List<Hecho> hechosFiltrados = hechos.stream()
          .filter(h -> categoria == null || (h.getCategoria() != null &&
              h.getCategoria().getNombre() != null &&
              h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase())))
          .filter(h -> ubicacion == null || (h.getLatitud() + "," + h.getLongitud()).equals(ubicacion))
          .filter(h -> fecha_reporte_desde == null || !h.getFechaDeCarga().toLocalDate().isBefore(fecha_reporte_desde))
          .filter(h -> fecha_reporte_hasta == null || !h.getFechaDeCarga().toLocalDate().isAfter(fecha_reporte_hasta))
          .filter(h -> fecha_acontecimiento_desde == null || !h.getFechaDeAcontecimiento().isBefore(fecha_acontecimiento_desde))
          .filter(h -> fecha_acontecimiento_hasta == null || !h.getFechaDeAcontecimiento().isAfter(fecha_acontecimiento_hasta))
          .toList();

      List<HechoDTO> hechosDTO = hechosFiltrados.stream()
          .map(Hecho::mapToDTO)
          .toList();

      return ResponseEntity.ok(hechosDTO);
    }

    // Si no está en locales, buscar en fuentes externas
    return fuentesService.getHechosColeccionByHandle(
        handle,
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion, modo
    );
  }

  @Override
  public ResponseEntity<?> actualizarFuentesDeColeccion(String handle, ActualizarFuentesColeccionDTO dto) {
    Coleccion coleccion = coleccionesRepository.findByHandle(handle);
    if (coleccion == null) {
      return ResponseEntity.badRequest().body("No se encontró la colección con handle: " + handle);
    }

    Criterio criterioExistente = coleccion.getCriterioDePertenencia();

    if (!(criterioExistente instanceof CriterioFuente criterioFuente)) {
      return ResponseEntity.badRequest().body("La colección no tiene un criterio de tipo CriterioFuente");
    }

    Long idFuente = dto.getIdFuente();
    String accion = dto.getAccion().toLowerCase();

    if ("AGREGAR".equals(accion)) {
      criterioFuente.agregarExclusion(idFuente);
      coleccionesRepository.actualizarColeccion(handle);
      return ResponseEntity.ok("Fuente " + idFuente + " agregada a las exclusiones de la colección");
    } else if ("ELIMINAR".equals(accion)) {
      criterioFuente.eliminarExclusion(idFuente);
      coleccionesRepository.actualizarColeccion(handle);
      return ResponseEntity.ok("Fuente " + idFuente + " eliminada de las exclusiones de la colección");
    } else {
      return ResponseEntity.badRequest().body("Acción no válida. Usar 'AGREGAR' o 'ELIMINAR'");
    }
  }

  @Override
  public void actualizarHechosSegunConsenso() {
      Collection<Coleccion> colecciones = this.coleccionesRepository.findAll();
      int fuentesTotales = this.fuentesService.cantidadDeFuentes();

      for (Coleccion coleccion : colecciones) {
        coleccion.setearHechosConsensuados(fuentesTotales);
        this.coleccionesRepository.save(coleccion);
      }

  }

  @Override
  public ResponseEntity<?> modificarCriterioColeccion(String handle, ModificarCriterioColeccionDTO dto) {
    Coleccion coleccion = coleccionesRepository.findByHandle(handle);
    if (coleccion == null) {
      return ResponseEntity.badRequest().body("No se encontro la coleccion con handle: " + handle);
    }

    Criterio nuevoCriterio = dto.getCriterio();

    // Actualizar el criterio de la coleccion
    coleccion.setCriterioDePertenencia(nuevoCriterio);

    // Actualizar los hechos de la coleccion segun el nuevo criterio
    coleccionesRepository.actualizarColeccion(handle);

    return ResponseEntity.ok("Criterio de la coleccion actualizado exitosamente");
  }

  @Override
  public ResponseEntity<ColeccionDTO> crearColeccion(CrearColeccionInputDTO dto){
      Criterio criterio = null;

      if (dto.getCriterioDePertenencia() != null) {
        criterio = criterioDePertenenciaFactory.crearCriterioDesdeDTO(dto.getCriterioDePertenencia());
      }

      AAlgoritmoConsenso algoritmoConsenso = null;

      if (dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isBlank()) {
          algoritmoConsenso = algoritmoConsensoFactory.crearAlgoritmo(dto.getAlgoritmoConsenso().toUpperCase());
      }
      // Si es null o vacío, algoritmoConsenso queda null
      Coleccion coleccion = new Coleccion(
              dto.getTitulo(),
              dto.getDescripcion(),
              criterio,
              algoritmoConsenso
      );

      coleccionesRepository.save(coleccion);
      coleccionesRepository.actualizarColeccion(coleccion.getHandle());

      // Setear hechos consensuados al crear la coleccion
      int fuentesTotales = this.fuentesService.cantidadDeFuentes();
      coleccion.setearHechosConsensuados(fuentesTotales);

      return ResponseEntity.ok(coleccion.mapToDTO());
  }

  @Override
  public ResponseEntity<?> actualizarColeccion(String handle, ActualizarColeccionInputDTO dto){
      Coleccion coleccion = coleccionesRepository.findByHandle(handle);
      if(coleccion == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Colección no encontrada");
      }
      boolean criterioModificado = false;

      //Titulo
      if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
        coleccion.setTitulo(dto.getTitulo());
      }
      //Descripcion
      if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
        coleccion.setDescripcion(dto.getDescripcion());
      }

      //CriterioPertenencia
      if(dto.getCriterioDePertenencia() != null){
        Criterio nuevoCriterio= criterioDePertenenciaFactory.crearCriterioDesdeDTO(dto.getCriterioDePertenencia());
        coleccion.setCriterioDePertenencia(nuevoCriterio);
        criterioModificado = true;
      }

      //AlgoritmoConcenso
      if(dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isBlank()){
        AAlgoritmoConsenso algoritmo = algoritmoConsensoFactory.crearAlgoritmo(dto.getAlgoritmoConsenso().toUpperCase());
        coleccion.setAlgoritmoConsenso(algoritmo);

        // Si se cambia el algoritmo, se actualizan los hechos consensuados
        int fuentesTotales = this.fuentesService.cantidadDeFuentes();
        coleccion.setearHechosConsensuados(fuentesTotales);
      }

      coleccionesRepository.update(coleccion);

      if(criterioModificado){
        coleccionesRepository.actualizarColeccion(handle);
      }

      return ResponseEntity.ok("Coleccion actualizada correctamente.");
  }

  @Override
  public ResponseEntity<?> eliminarColeccion(String handle){
      Coleccion coleccion = coleccionesRepository.findByHandle(handle);
    if(coleccion == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Colección no encontrada");
    }

    coleccionesRepository.delete(coleccion);
    return ResponseEntity.ok("Coleccion eliminada correctamente.");
  }

  /*public TipoAlgoritmoConsenso parsearAlgoritmo(String valor){
      if(valor == null) return null;

      valor= valor.trim();
      if(valor.isEmpty()){
        throw new IllegalArgumentException("El algoritmo no puede ser una cadena vacia.");
      }

      try{
        return TipoAlgoritmoConsenso.valueOf(valor.toUpperCase());
      }catch(IllegalArgumentException e){
        throw new IllegalArgumentException("El algoritmo no es valido, debe ser uno de:"+ Arrays.toString(TipoAlgoritmoConsenso.values()));
      }
  }*/


}

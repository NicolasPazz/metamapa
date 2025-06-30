package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioFuente;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioCombinado;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ActualizarFuentesColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ModificarCriterioColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionesService {

    private final IColeccionesRepository coleccionesRepository;
    private final FuentesService fuentesService;

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

  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion) {
    // Primero, buscar en local
    Coleccion local = coleccionesRepository.findByHandle(handle);

    if (local != null) {
      List<Hecho> hechos = local.getHechos().stream()
          .filter(h -> categoria == null || (h.getCategoria() != null &&
              h.getCategoria().getNombre() != null &&
              h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase())))
          .filter(h -> ubicacion == null || (h.getLatitud() + "," + h.getLongitud()).equals(ubicacion))
          .filter(h -> fecha_reporte_desde == null || !h.getFechaDeCarga().toLocalDate().isBefore(fecha_reporte_desde))
          .filter(h -> fecha_reporte_hasta == null || !h.getFechaDeCarga().toLocalDate().isAfter(fecha_reporte_hasta))
          .filter(h -> fecha_acontecimiento_desde == null || !h.getFechaDeAcontecimiento().isBefore(fecha_acontecimiento_desde))
          .filter(h -> fecha_acontecimiento_hasta == null || !h.getFechaDeAcontecimiento().isAfter(fecha_acontecimiento_hasta))
          .toList();

      List<HechoDTO> hechosDTO = hechos.stream()
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
        ubicacion
    );
  }

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
}

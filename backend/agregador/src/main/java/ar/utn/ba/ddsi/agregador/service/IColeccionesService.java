package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ActualizarColeccionInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ActualizarFuentesColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.CrearColeccionInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.ModificarCriterioColeccionDTO;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.List;

public interface IColeccionesService {
    ResponseEntity<List<ColeccionDTO>> getAll();
    ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
        String handle,
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
        String ubicacion,
        String modo);
    ResponseEntity<?> actualizarFuentesDeColeccion(String handle, ActualizarFuentesColeccionDTO dto);
    void actualizarHechosSegunConsenso();
    ResponseEntity<?> modificarCriterioColeccion(String handle, ModificarCriterioColeccionDTO dto);
    ResponseEntity<ColeccionDTO> crearColeccion(CrearColeccionInputDTO dto);
    ResponseEntity<?> eliminarColeccion(String handle);
    ResponseEntity<?> actualizarColeccion(String handle, ActualizarColeccionInputDTO dto);
}
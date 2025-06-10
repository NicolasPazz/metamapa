package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.TransicionEstadoSolicitud;
import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import ar.utn.ba.ddsi.dinamica.service.ISolicitudesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {

    private final ISolicitudesRepository solicitudesRepository;
    private final IFuenteService hechosService;

    @Override
    public List<SolicitudOutputDTO> buscarTodas(){
        List<SolicitudHecho> solicitudes = this.solicitudesRepository.findAll();
        return solicitudes
                .stream()
                .map(this::solicitudOutputDTO)
                .toList();
    }


    @Override
    public Long recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input) {
        SolicitudHecho solicitud = new SolicitudHecho();
        solicitud.setIdContribuyente(input.getIdContribuyente());
        solicitud.setTitulo(input.getTitulo());
        solicitud.setDescripcion(input.getDescripcion());
        solicitud.setCategoria(input.getCategoria());
        solicitud.setLatitud(input.getLatitud());
        solicitud.setLongitud(input.getLongitud());
        solicitud.setFechaDeAcontecimiento(LocalDate.parse(input.getFechaDeAcontecimiento()));

        List<ContenidoMultimediaInputDTO> multimedia = input.getMultimedia();
        if (multimedia != null) {
            for (ContenidoMultimediaInputDTO item : multimedia) {
                String tipoStr = item.getTipoContenido();
                if (tipoStr == null || tipoStr.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede estar vacío");
                }

                try {
                    TipoContenido.valueOf(tipoStr.toUpperCase()); // Validamos si es un tipo válido
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Tipo de contenido inválido: " + tipoStr + ". Los tipos válidos son: " + Arrays.toString(TipoContenido.values())
                    );
                }
            }
        }

        solicitud.setMultimedia(input.getMultimedia());
        solicitud.setFechaDeCargaSolicitud(LocalDateTime.now());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setSugerencia(null);
        solicitud.setTransiciones(new ArrayList<>());


        return solicitudesRepository.save(solicitud);   //asigno id
    }

    @Override
    public SolicitudOutputDTO obtenerSolicitud(Long id) {
        //TODO: cuando se modelen admin y users validar que se cuente con los permisos
        SolicitudHecho solicitud = solicitudesRepository.findById(id);
        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
        }
        return solicitudOutputDTO(solicitud);
    }

    private SolicitudOutputDTO solicitudOutputDTO(SolicitudHecho solicitud){
        var dto = new SolicitudOutputDTO();
        dto.setIdSolicitud(solicitud.getIdSolicitud());
        dto.setIdContribuyente(solicitud.getIdContribuyente());
        dto.setTitulo(solicitud.getTitulo());
        dto.setDescripcion(solicitud.getDescripcion());
        dto.setNombreCategoria(solicitud.getCategoria());
        dto.setLatitud(solicitud.getLatitud());
        dto.setLongitud(solicitud.getLongitud());
        dto.setFechaDeAcontecimiento(solicitud.getFechaDeAcontecimiento().toString());
        dto.setContenidoMultimedia(solicitud.getMultimedia());
        dto.setFechaDeCargaSolicitud(solicitud.getFechaDeCargaSolicitud().toString());
        dto.setEstado(solicitud.getEstado().toString());
        dto.setSugerencia(solicitud.getSugerencia());

        return dto;
    }

    @Override
    public SolicitudOutputDTO modificarSolicitud(Long idSolicitud, SolicitudModificarUserInputDTO inputDTO) {
        SolicitudHecho solicitud = solicitudesRepository.findById(idSolicitud);

        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
        }

        if(inputDTO.getIdContribuyente() == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para modificar esta solicitud");
        }

        // Validar que sea el mismo contribuyente(completar al agregar base de datos contribuyente)
        if (!solicitud.getIdContribuyente().equals(inputDTO.getIdContribuyente())) {
            //throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para modificar esta solicitud");

        }

        // Validar que no hayan pasado más de 7 días desde la fecha de carga
        if (solicitud.getFechaDeCargaSolicitud().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya no puede ser modificada");
        }

        // Modificar solo si los campos no vienen nulos
        if (inputDTO.getNuevoTitulo() != null)
            solicitud.setTitulo(inputDTO.getNuevoTitulo());

        if (inputDTO.getNuevoDescripcion() != null)
            solicitud.setDescripcion(inputDTO.getNuevoDescripcion());

        if (inputDTO.getNuevoNombreCategoria() != null)
            solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());

        if (inputDTO.getNuevoLatitud() != null)
            solicitud.setLatitud(inputDTO.getNuevoLatitud());

        if (inputDTO.getNuevoLongitud() != null)
            solicitud.setLongitud(inputDTO.getNuevoLongitud());

        if (inputDTO.getNuevoFechaDeAcontecimiento() != null)
            solicitud.setFechaDeAcontecimiento(LocalDate.parse(inputDTO.getNuevoFechaDeAcontecimiento()));

        /*if (inputDTO.getNuevoContenidoMultimedia() != null)
            solicitud.setMultimedia(inputDTO.getNuevoContenidoMultimedia());*/

        List<ContenidoMultimediaInputDTO> nuevoMultimedia = inputDTO.getNuevoContenidoMultimedia();
        if (nuevoMultimedia != null) {
            for (ContenidoMultimediaInputDTO item : nuevoMultimedia) {
                String tipoStr = item.getTipoContenido();
                if (tipoStr == null || tipoStr.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede estar vacío");
                }

                try {
                    TipoContenido.valueOf(tipoStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Tipo de contenido inválido: " + tipoStr + ". Tipos válidos: " + Arrays.toString(TipoContenido.values())
                    );
                }
            }

            // Solo si pasó la validación se actualiza
            solicitud.setMultimedia(nuevoMultimedia);
        }


        // Cambiar estado a PENDIENTE
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        // Agregar la transición de estado
        if (solicitud.getTransiciones() == null) {
            solicitud.setTransiciones(new ArrayList<>());
        }
        solicitud.getTransiciones().add(new TransicionEstadoSolicitud(EstadoSolicitud.PENDIENTE));

        // Guardar cambios
        solicitudesRepository.update(solicitud);

        return solicitudOutputDTO(solicitud);
    }

    @Override
    public SolicitudOutputDTO revisionSolicitud(Long idSolicitud, SolicitudModificarAdminInputDTO inputDTO) {
        SolicitudHecho solicitud = solicitudesRepository.findById(idSolicitud);

        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
        }

        // Validar que la solicitud esté en estado PENDIENTE antes de modificarla
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo solicitudes en estado PENDIENTE pueden ser modificadas por admin");
        }

        String estadoStr = inputDTO.getNuevoEstadoSolicitud();
        EstadoSolicitud estado = EstadoSolicitud.valueOf(estadoStr.toUpperCase());

        // Actualizar estado y sugerencia
        solicitud.setEstado(estado);
        solicitud.setSugerencia(inputDTO.getNuevoSugerencia());

        // Agregar transición de estado
        solicitud.getTransiciones().add(new TransicionEstadoSolicitud(estado));

        // Guardar cambios
        solicitudesRepository.update(solicitud);

        // Si el estado es ACEPTADO o ACEPTADO_SUGERIDO, crear el hecho asociado (puede ser otro servicio)
        if (estado == EstadoSolicitud.ACEPTADO || estado == EstadoSolicitud.ACEPTADO_SUGERIDO) {
            hechosService.crearHechoDesdeSolicitud(solicitud);
        }
        return solicitudOutputDTO(solicitud);
    }
}

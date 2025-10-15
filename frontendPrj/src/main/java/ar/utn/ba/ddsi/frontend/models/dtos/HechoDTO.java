package ar.utn.ba.ddsi.frontend.models.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HechoDTO {
    private Long id;
    private String origen;
    private String estado;
    private Long contribuyente; //   private ContribuyenteDTO contribuyente;
    private List<String> etiquetas;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private LocalDate fechaDeAcontecimiento;
    private LocalDateTime fechaDeCarga;
    private LocalDateTime ultimaModificacion;
    private List<ContenidoMultimediaDTO> contenidoMultimedia;
    private List<TransicionEstadoHechoDTO> transicionesDeEstado;
}

package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SolicitudHecho {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idSolicitud;
    @Column(name = "id_contribuyente")
    private Long idContribuyente;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "categoria")
    private String categoria;
    @Column(name = "latitud")
    private double latitud;
    @Column(name = "longitud")
    private double longitud;
    @Column(name = "fechaDeAcontecimiento")
    private LocalDate fechaDeAcontecimiento;
    @OneToMany
    @JoinColumn(name = "multimedia_id")
    private List<ContenidoMultimediaInputDTO> multimedia;
    @Column(name = "fechaDeCarga")
    private LocalDateTime fechaDeCargaSolicitud;
    @Enumerated(EnumType.ORDINAL)
    private EstadoSolicitud estado; //pendiente-aceptado-rechazado
    @Column(name = "sugerencia")
    private String sugerencia;
    @ElementCollection
    private List<TransicionEstadoSolicitud> transiciones;

}

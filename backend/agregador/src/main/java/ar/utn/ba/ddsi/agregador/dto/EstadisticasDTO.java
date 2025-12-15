package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    @JsonProperty("categoria_con_mas_hechos")
    private CategoriaEstadisticaDTO categoriaConMasHechos;

    @JsonProperty("provincia_con_mas_hechos_por_coleccion")
    private Map<String, ProvinciaEstadisticaDTO> provinciaConMasHechosPorColeccion;

    @JsonProperty("provincia_con_mas_hechos_por_categoria")
    private Map<String, ProvinciaEstadisticaDTO> provinciaConMasHechosPorCategoria;

    @JsonProperty("horas_pico_por_categoria")
    private Map<String, HoraEstadisticaDTO> horasPicoPorCategoria;

    @JsonProperty("total_solicitudes_spam")
    private Long totalSolicitudesSpam;

    @JsonProperty("fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;
}


package ar.utn.ba.ddsi.frontend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasCompletasDTO {

  private List<ColeccionProvinciaDTO> distribucionGeograficaColecciones;

  private CategoriaDominanteDTO categoriaDominante;

  private List<CategoriaProvinciaDTO> concentracionGeograficaCategorias;

  private List<CategoriaHoraDTO> distribucionTemporalCategorias;

  private SpamDTO estadisticaSpam;
}


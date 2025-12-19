package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearColeccionDTO {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @JsonProperty("criterios")
    private List<CriterioInputDTO> criterios;

    @JsonProperty("algoritmo_consenso")
    private String algoritmoConsenso;

    @JsonProperty("fuente_id")
    private Long fuenteId;

    @JsonIgnore
    public boolean isValid() {
        if (criterios == null || criterios.isEmpty()) return false;

        // Validate EVERY criterion in the list
        for (CriterioInputDTO c : criterios) {
            if (!isCriterioValid(c)) return false;
        }
        return true;
    }

    private boolean isCriterioValid(CriterioInputDTO c) {
        String tipo = c.getTipo();
        if ("fecha".equals(tipo)) return c.getFechaInicial() != null;
        if ("lugar".equals(tipo)) return c.getLat() != null && c.getLng() != null;
        if ("igualdad".equals(tipo)) return c.getCadenaAComparar() != null && c.getTipoDeDato() != null;
        if ("origen".equals(tipo)) return c.getOrigen() != null;
        if ("fuente".equals(tipo)) return c.getFuentes() != null;
        return true;
    }
}
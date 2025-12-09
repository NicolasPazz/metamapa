package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CrearColeccionDTO {

    @NotBlank(message = "El titulo es obligatorio")
    @JsonProperty("titulo")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("criterioDePertenencia")
    private CriterioInputDTO criterioDePertenencia;

    @JsonProperty("algoritmoConsenso")
    private String algoritmoConsenso;

    @JsonIgnore
    public boolean isValid() {
        if (criterioDePertenencia == null) return false;

        String tipo = criterioDePertenencia.getTipo();

        if ("fecha".equals(tipo)) {
            // Enforce that dates exist ONLY if type is 'fecha'
            return criterioDePertenencia.getFechaInicial() != null;
        }
        if ("lugar".equals(tipo)) {
            return criterioDePertenencia.getLat() != null && criterioDePertenencia.getLng() != null;
        }
        if ("igualdad".equals(tipo)) {
            return criterioDePertenencia.getCadenaAComparar() != null && criterioDePertenencia.getTipoDeDato() != null;
        }
        if ("origen".equals(tipo)) {
            return criterioDePertenencia.getOrigen() != null;
        }
        return true;
    }
}
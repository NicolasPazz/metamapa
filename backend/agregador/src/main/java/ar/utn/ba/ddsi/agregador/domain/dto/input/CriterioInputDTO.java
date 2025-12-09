package ar.utn.ba.ddsi.agregador.domain.dto.input;

import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Valid
@Getter
@Setter
@Data
@NoArgsConstructor
public class CriterioInputDTO {

    @JsonProperty("tipo")
    private String tipo;

    //agregar campos segun el tipo

    //CriterioFecha
    @PastOrPresent(message = "La fecha no puede ser futura")
    @JsonProperty("fecha_inicial")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaInicial;
    //@PastOrPresent(message = "La fecha no puede ser futura")
    @JsonProperty("fecha_final")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaFinal;
    @JsonProperty("tipo_fecha")
    private String tipoFecha;

    //CriterioIgualdad
    @JsonProperty("cadena_a_comparar")
    private String cadenaAComparar;
    @JsonProperty("tipo_dato")
    private String tipoDeDato;

    //CriterioLugar
    @JsonProperty("latitud")
    private Double lat;
    @JsonProperty("longitud")
    private Double lng;

    //CriterioOrigen
    @JsonProperty("origen")
    private String origen;

}

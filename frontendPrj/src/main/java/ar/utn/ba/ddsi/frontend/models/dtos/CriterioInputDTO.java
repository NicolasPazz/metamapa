package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Valid
@Getter
@Setter
@Data
@AllArgsConstructor
public class CriterioInputDTO {

    @JsonProperty("tipo")
    private String tipo;

    //agregar campos segun el tipo

    //CriterioFecha
    @PastOrPresent(message = "La fecha no puede ser futura")
    @JsonProperty("fecha_inicial")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaInicial;
    //@PastOrPresent(message = "La fecha no puede ser futura")
    @JsonProperty("fecha_final")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
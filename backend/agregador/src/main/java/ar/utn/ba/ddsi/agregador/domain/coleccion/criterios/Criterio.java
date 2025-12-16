package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CriterioFechas.class, name = "FECHAS"),
    @JsonSubTypes.Type(value = CriterioLugar.class, name = "LUGAR"),
    @JsonSubTypes.Type(value = CriterioFuente.class, name = "FUENTE"),
    @JsonSubTypes.Type(value = CriterioIgualdad.class, name = "IGUALDAD"),
    @JsonSubTypes.Type(value = CriterioOrigen.class, name = "ORIGEN")
})
public interface Criterio {
  boolean cumple(Hecho hecho);
}

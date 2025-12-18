package ar.utn.ba.ddsi.agregador.repositorie.converter;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.Absoluta;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MultiplesMenciones;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import java.util.function.Supplier;

@Converter
public class AlgoritmoAttributeConverter implements AttributeConverter<AAlgoritmoConsenso, String> {
    private static final Map<TipoAlgoritmoConsenso, Supplier<AAlgoritmoConsenso>> INSTANCIAS = Map.of(
        TipoAlgoritmoConsenso.ABSOLUTA, Absoluta::getInstance,
        TipoAlgoritmoConsenso.MAYORIA_SIMPLE, MayoriaSimple::getInstance,
        TipoAlgoritmoConsenso.MULTIPLES_MENCIONES, MultiplesMenciones::getInstance
    );

    @Override
    public String convertToDatabaseColumn(AAlgoritmoConsenso algoritmo) {
      if (algoritmo == null) return null;
      return algoritmo.getNombre().name();
    }

    @Override
    public AAlgoritmoConsenso convertToEntityAttribute(String value) {
      if (value == null || value.isBlank()) return null;
      TipoAlgoritmoConsenso tipo = TipoAlgoritmoConsenso.valueOf(value.toUpperCase());
      return INSTANCIAS.getOrDefault(tipo, () -> {
        throw new IllegalStateException("Tipo de algoritmo no soportado: " + tipo);
      }).get();
    }
}

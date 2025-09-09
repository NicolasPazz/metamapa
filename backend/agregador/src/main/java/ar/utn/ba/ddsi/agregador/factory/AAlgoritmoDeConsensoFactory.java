package ar.utn.ba.ddsi.agregador.factory;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.absoluta;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.mayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.multiplesMenciones;
import ar.utn.ba.ddsi.agregador.domain.coleccion.TipoAlgoritmoConsenso;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AAlgoritmoDeConsensoFactory {
    public AAlgoritmoConsenso crearAlgoritmo(String tipoAlgoritmo) {

        if (tipoAlgoritmo == null || tipoAlgoritmo.isBlank()){
            throw new IllegalArgumentException("El nombre del algoritmo no puede estar vacío. Valores válidos: " + Arrays.toString(TipoAlgoritmoConsenso.values()));
        }

        try{
            TipoAlgoritmoConsenso tipo = TipoAlgoritmoConsenso.valueOf(tipoAlgoritmo);

            return switch(tipo){
                case MULTIPLES_MENCIONES -> new multiplesMenciones();
                case MAYORIA_SIMPLE -> new mayoriaSimple();
                case ABSOLUTA -> new absoluta();
            };
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Algoritmo desconocido: " + tipoAlgoritmo + ". Valores válidos: " + Arrays.toString(TipoAlgoritmoConsenso.values()));
        }
    }
}

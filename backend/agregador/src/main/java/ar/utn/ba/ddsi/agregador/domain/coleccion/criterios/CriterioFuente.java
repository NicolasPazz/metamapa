package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriterioFuente implements Criterio {
    @JsonIgnore
    private final TipoCriterio tipo = TipoCriterio.FUENTE;
    private Set<Long> fuentes = new HashSet<>();
    private Set<String> nombresFuentes = new HashSet<>();

    public CriterioFuente(Long idFuente) {
        this.fuentes.add(idFuente);
    }

    public void agregarFuente(Long idFuente) {
        fuentes.add(idFuente);
    }

    public void eliminarFuente(Long idFuente) {
        fuentes.remove(idFuente);
    }

    public void agregarNombreFuente(String nombreFuente) {
        nombresFuentes.add(nombreFuente);
    }

    public void eliminarNombreFuente(String nombreFuente) {
        nombresFuentes.remove(nombreFuente);
    }

    @Override
    public boolean cumple(Hecho hecho) {
        // Si no hay fuentes especificadas (ni IDs ni nombres), aceptar todos los hechos
        boolean tieneIds = fuentes != null && !fuentes.isEmpty();
        boolean tieneNombres = nombresFuentes != null && !nombresFuentes.isEmpty();
        
        if (!tieneIds && !tieneNombres) {
            return true;
        }

        // Verificar por ID
        if (tieneIds) {
            Long idFuente = hecho.getFuenteId();
            if (fuentes.contains(idFuente)) {
                return true;
            }
        }

        // Verificar por nombre
        if (tieneNombres) {
            String nombreFuente = hecho.getNombreFuente();
            if (nombreFuente != null && nombresFuentes.contains(nombreFuente)) {
                return true;
            }
        }

        // Si tiene criterios pero no coincide ni por ID ni por nombre, no cumple
        return false;
    }
}
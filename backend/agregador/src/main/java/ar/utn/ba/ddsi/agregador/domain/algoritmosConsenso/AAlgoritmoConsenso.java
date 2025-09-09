package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;
import java.util.Objects;

public abstract class AAlgoritmoConsenso {

    public abstract Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos);

    public int contarApariciones(Hecho hecho, List<Hecho> hechos) {
        int cont = 0;

        for(Hecho vHecho : hechos) {
            if(sonSimilares(hecho,vHecho)) cont++;
        }

        return cont;
    }

    private boolean sonSimilares(Hecho h1, Hecho h2) {
        return igualesIgnoreCaseTrim(h1.getTitulo(), h2.getTitulo()) &&
                igualesIgnoreCaseTrim(h1.getDescripcion(), h2.getDescripcion()) &&
                Objects.equals(h1.getLatitud(), h2.getLatitud()) &&
                Objects.equals(h1.getLongitud(), h2.getLongitud()) &&
                Objects.equals(h1.getFechaDeAcontecimiento(), h2.getFechaDeAcontecimiento());
    }

    private boolean igualesIgnoreCaseTrim(String a, String b) {
        if (a == null || b == null) return Objects.equals(a, b);
        return a.trim().equalsIgnoreCase(b.trim());
    }

    public long contarConTituloIgual(Hecho hecho, List<Hecho> hechos) {
        String titulo = hecho.getTitulo().toLowerCase().trim();
        return hechos.stream()
                .filter(h -> h.getTitulo() != null && h.getTitulo().toLowerCase().trim().equals(titulo))
                .count();
    }

    public boolean tieneConflictosDeTitulo(Hecho hecho, List<Hecho> hechos) {
        return contarApariciones(hecho, hechos) != contarConTituloIgual(hecho, hechos);
    }




    public Boolean aparicionesMayorA(Hecho hecho, int comparador, List<Hecho> hechos) {
        return this.contarApariciones(hecho, hechos) >= comparador;
    }
}

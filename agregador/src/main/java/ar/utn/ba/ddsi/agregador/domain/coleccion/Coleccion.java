package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class Coleccion {
  @Getter
  private String handle;
  @Getter
  @Setter
  private String titulo;
  @Getter
  @Setter
  private String descripcion;
  @Setter
  private List<Hecho> hechos;
  @Getter
  @Setter
  private Criterio criterioDePertenencia;

  public Coleccion(String titulo, String descripcion, Criterio criterioDePertenencia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterioDePertenencia = criterioDePertenencia;
    this.hechos = hechos.stream().filter(criterioDePertenencia::cumple).toList();
    this.handle = generarHandle(titulo);
  }

  public Coleccion(String titulo, String descripcion, List<Hecho> hechos) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.hechos = hechos;
    this.handle = generarHandle(titulo);
  }

  public List<Hecho> getHechos() {
    if (criterioDePertenencia == null) {
      return this.hechos.stream()
          .filter(Hecho::esVisible)
          .toList();
    }

    return this.hechos.stream()
        .filter(criterioDePertenencia::cumple)
        .filter(Hecho::esVisible)
        .toList();
  }

  private String generarHandle(String titulo) {
    String base = titulo.toLowerCase()
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", "");

    String sufijo = UUID.randomUUID().toString().substring(0, 8);
    return base + "-" + sufijo;
  }

  public ColeccionDTO mapToDTO() {
    ColeccionDTO dto = new ColeccionDTO();
    dto.setHandle(this.getHandle());
    dto.setTitulo(this.getTitulo());
    dto.setDescripcion(this.getDescripcion());
    List<HechoDTO> hechosDTO = this.getHechos().stream()
        .map(Hecho::mapToDTO)
        .toList();
    dto.setHechos(hechosDTO);
    return dto;
  }

  public void actualizar() {
    if (this.criterioDePertenencia != null) {
      this.hechos = this.hechos.stream()
          .filter(criterioDePertenencia::cumple)
          .filter(Hecho::esVisible)
          .toList();
    }
  }
}

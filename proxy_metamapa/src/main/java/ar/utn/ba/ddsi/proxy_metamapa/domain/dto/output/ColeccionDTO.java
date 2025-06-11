package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ColeccionDTO {
  public String handle;
  public String titulo;
  public String descripcion;
  public List<HechoDTO> hechos;

  public ColeccionDTO(String titulo, String descripcion, List<HechoDTO> hechos) {
    this.handle = generarHandle(titulo);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.hechos = hechos;
  }

  private String generarHandle(String titulo) {
    String base = titulo.toLowerCase()
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", "");

    String sufijo = UUID.randomUUID().toString().substring(0, 8);
    return base + "-" + sufijo;
  }
}
package ar.utn.ba.ddsi.fuenteestatica.domain.hecho;

import ar.utn.ba.ddsi.fuenteestatica.domain.user.Contribuyente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {
  private Long id;
  private DataHecho data;
  private OrigenHecho origen;
  private EstadoHecho estado;
  private Contribuyente contribuyente;
  private Set<Etiqueta> etiquetas = new HashSet<>();
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();
  private Categoria categoria;

  // CSV
  public Hecho(DataHecho data, OrigenHecho origen) {
    this.data = data;
    this.origen = origen;
    transicionarEstado(EstadoHecho.VISIBLE);
    this.categoria = new Categoria(data.getNombreCategoria());
    this.contribuyente = null;
  }

  // MANUAL / CONTRIBUYENTE
  public Hecho(String titulo, String descripcion, Categoria categoria, LocalDate fechaDeAcontecimiento, double latitud, double longitud, List<ContenidoMultimedia> multimedia, Set<Etiqueta> etiquetas, Contribuyente contribuyente) {
    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(titulo);
    dataHecho.setDescripcion(descripcion);
    dataHecho.setFechaDeAcontecimiento(fechaDeAcontecimiento);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);
    dataHecho.setNombreCategoria(categoria.getNombre());

    this.data = dataHecho;
    this.categoria = categoria;
    this.multimedia = multimedia;
    this.etiquetas = etiquetas;
    this.contribuyente = contribuyente;

    if(contribuyente != null) {
      this.origen = OrigenHecho.CONTRIBUYENTE;
    } else {
      this.origen = OrigenHecho.MANUAL;
    }

    transicionarEstado(EstadoHecho.VISIBLE);
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    return this.getTitulo().equalsIgnoreCase(otroHecho.getTitulo());
  }

  public void ocultar() {
    this.estado = EstadoHecho.OCULTO;
  }

  public boolean esVisible() {
    // TODO: verificar que no haya una solicitud de eliminacion aceptada
    return this.estado == EstadoHecho.VISIBLE;
  }

  public void agregarEtiqueta(String etiq) {
    //TODO: verificar que la etiqueta no exista
    if (etiq != null && !etiq.isBlank()) {
      Etiqueta nueva = new Etiqueta(etiq);
      this.etiquetas.add(nueva);
    }
  }

  public boolean contieneEtiqueta(String etiqueta) {
    return this.etiquetas.contains(new Etiqueta(etiqueta));
  }

  public boolean esDeCategoria(Categoria categoria) {
    return this.categoria.equals(categoria);
  }

  public void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado, LocalDate.now()));
    this.estado = estado;
  }

  // TODO: revisar si no conviene hacer una clase en estos dos casos
  public List<Long> getIdEtiquetas() {
    List<Long> ids = new ArrayList<>();
    for (Etiqueta etiqueta : this.etiquetas) {
      ids.add(etiqueta.getId());
    }
    return ids;
  }
  public List<String> getMultimediaPaths() {
    List<String> paths = new ArrayList<>();
    for (ContenidoMultimedia multimedia : this.multimedia) {
      paths.add(multimedia.getPathArchivo());
    }
    return paths;
  }

  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
  }

  public Double getLatitud() {
    return data.getLatitud();
  }

  public Double getLongitud() {
    return data.getLongitud();
  }

  public LocalDate getFechaDeAcontecimiento() {
    return data.getFechaDeAcontecimiento();
  }

  public LocalDate getFechaDeCarga() {
    return data.getFechaDeCarga();
  }
}
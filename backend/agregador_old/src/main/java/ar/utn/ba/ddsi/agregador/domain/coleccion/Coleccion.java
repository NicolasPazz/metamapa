package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.converter.AlgoritmoAttributeConverter;
import ar.utn.ba.ddsi.agregador.converter.CriterioAttributeConverter;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;

import java.util.ArrayList;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String handle;
  @Column(name = "titulo", nullable = false)
  private String titulo;
  @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
  private String descripcion;
  @OneToMany
  @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle")
  private List<Hecho> hechos;
  @Convert(converter = CriterioAttributeConverter.class)
  private Criterio criterioDePertenencia;
  @Convert(converter = AlgoritmoAttributeConverter.class)
  private AAlgoritmoConsenso algoritmoConsenso;
  @OneToMany
  @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle")
  private List<Hecho> hechosConsensuados;

  public Coleccion(String titulo, String descripcion, Criterio criterioDePertenencia, AAlgoritmoConsenso algoritmoConsenso) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterioDePertenencia = criterioDePertenencia;
    this.hechos = new ArrayList<>();
    this.handle = generarHandle(titulo);
    this.algoritmoConsenso = algoritmoConsenso;
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

  public void setearHechosConsensuados(int fuentesTotales) {
    this.setHechosConsensuados(this
            .getHechos()
            .stream()
            .filter(hecho -> this.algoritmoConsenso.esConsensuado(hecho, fuentesTotales, this.hechos))
            .toList());
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

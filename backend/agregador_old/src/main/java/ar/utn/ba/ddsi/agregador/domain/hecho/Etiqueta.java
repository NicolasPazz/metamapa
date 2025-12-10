package ar.utn.ba.ddsi.agregador.domain.hecho;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "etiqueta")
public class Etiqueta {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre", nullable = false)
  private String nombre;

  public Etiqueta(String nombre) {
    this.nombre = nombre.trim().toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Etiqueta etiqueta = (Etiqueta) o;
    return Objects.equals(nombre, etiqueta.nombre);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre);
  }
}
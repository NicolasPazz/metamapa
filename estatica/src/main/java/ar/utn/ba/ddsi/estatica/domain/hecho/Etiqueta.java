package ar.utn.ba.ddsi.estatica.domain.hecho;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Etiqueta {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
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
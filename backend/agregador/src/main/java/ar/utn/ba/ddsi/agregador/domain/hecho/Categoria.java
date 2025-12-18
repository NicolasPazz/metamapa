package ar.utn.ba.ddsi.agregador.domain.hecho;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false, unique = true)
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = normalizar(nombre);
  }

  public static String normalizar(String nombre) {
    if (nombre == null) {
      return null;
    }
    return nombre.trim().toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Categoria categoria = (Categoria) o;
    return nombre.equals(categoria.nombre);
  }

  @Override
  public int hashCode() {
    return nombre.hashCode();
  }
}
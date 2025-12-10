package ar.utn.ba.ddsi.agregador.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contribuyente")
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre", nullable = true)
  private String nombre;
  @Column(name = "apellido", nullable = true)
  private String apellido;
  @Column(name = "fechaNacimiento", nullable = true)
  private LocalDate fechaDeNacimiento;

  public Contribuyente(String nombre) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
  }

  public Contribuyente(String nombre, String apellido, LocalDate fechaDeNacimiento) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaDeNacimiento = fechaDeNacimiento;
  }

  public boolean esAnonimo() {
    return this.nombre != null;
  }

  public Integer edad() {
    if (fechaDeNacimiento == null) {
      return null; // No se puede calcular la edad si no hay fecha de nacimiento
    }
    return LocalDate.now().getYear() - fechaDeNacimiento.getYear();
  }
}
package ar.utn.ba.ddsi.agregador.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Contribuyente {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
  private String nombre;
  @Column(name = "apellido")
  private String apellido;
  @Column(name = "fechaNacimiento")
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
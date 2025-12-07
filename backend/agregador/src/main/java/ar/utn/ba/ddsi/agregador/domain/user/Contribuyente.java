package ar.utn.ba.ddsi.agregador.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
  private String nombre;
  @Column(name = "apellido")
  private String apellido;
  @Column(name = "fechaNacimiento")
  private LocalDate fechaDeNacimiento;

  public Contribuyente(String nombre, String apellido, LocalDate fechaDeNacimiento) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaDeNacimiento = fechaDeNacimiento;
  }

  public boolean esAnonimo() {
    return this.nombre == null;
  }
}
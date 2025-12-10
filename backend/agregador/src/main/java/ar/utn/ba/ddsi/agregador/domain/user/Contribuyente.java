package ar.utn.ba.ddsi.agregador.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "contribuyente")
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = true)
  private String nombre;

  @Column(name = "apellido", nullable = true)
  private String apellido;

  @Column(name = "fecha_nacimiento", nullable = true)
  private LocalDate fechaDeNacimiento;

  @Column(name = "fecha_de_carga", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime fechaDeCarga;

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
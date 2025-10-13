package ar.utn.ba.ddsi.dinamica.domain.user;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
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
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
  private String nombre;
  @Column(name = "apellido")
  private String apellido;
  @Column(name = "fechaNacimiento")
  private LocalDate fechaDeNacimiento;  //mejor esto que edad

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

  public void subirHecho(Hecho hecho) {
    // TODO
    if (!this.esAnonimo()) {
      // TODO: almacenar datos personales y poner a disposicion de administradores
    }
  }
}
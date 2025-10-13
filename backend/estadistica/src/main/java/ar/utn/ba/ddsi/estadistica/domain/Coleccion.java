package ar.utn.ba.ddsi.estadistica.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  @Column(name = "handle", columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
  private String handle;

  @Column(name = "titulo")
  private String titulo;

  @Transient
  private List<Hecho> hechos = new ArrayList<>();
}

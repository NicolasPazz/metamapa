package ar.utn.ba.ddsi.estatica.domain.hecho;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CsvBindByName(column = "Título")
  private String titulo;

  @CsvBindByName(column = "Descripción")
  @Column(columnDefinition = "TEXT", nullable = false)
  private String descripcion;

  @CsvBindByName(column = "Categoría")
  private String nombreCategoria;

  @CsvBindByName(column = "Latitud")
  private Double latitud;

  @CsvBindByName(column = "Longitud")
  private Double longitud;

  @CsvDate("dd/MM/yyyy")
  @CsvBindByName(column = "Fecha del hecho")
  private LocalDate fechaDeAcontecimiento;

  private LocalDateTime fechaDeCarga;

  @Enumerated(EnumType.STRING)
  @Column(name="origen", nullable = false)
  private OrigenHecho origen;

  private Boolean enviado;

  public Hecho() {
    this.fechaDeCarga = LocalDateTime.now();
    this.origen = OrigenHecho.DATASET;
    this.enviado = false;
  }

  public HechoOutputDTO toOutputDTO() {
    return new HechoOutputDTO(
        this.id,
        this.titulo,
        this.descripcion,
        this.nombreCategoria,
        this.latitud,
        this.longitud,
        this.fechaDeAcontecimiento,
        this.fechaDeCarga
    );
  }
}
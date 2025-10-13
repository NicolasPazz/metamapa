package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ContenidoMultimediaInputDTO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @Column(name = "path")
    String pathArchivo;
    @Column(name = "tipoContenido")
    String tipoContenido;
}

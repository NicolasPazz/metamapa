package ar.utn.ba.ddsi.agregador.domain.fuente;
}
    private Boolean activa = true;
    @Builder.Default
    @Column(name = "activa", nullable = false)

    private TipoFuente tipo;
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)

    private String url;
    @Column(name = "url", nullable = false)

    private String nombre;
    @Column(name = "nombre", nullable = false)

    private Long id;
    @Id
public class Fuente {
@Table(name = "fuente")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter

import lombok.*;
import jakarta.persistence.*;
import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;



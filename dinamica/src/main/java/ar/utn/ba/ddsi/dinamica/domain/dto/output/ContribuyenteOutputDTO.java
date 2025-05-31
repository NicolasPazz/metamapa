package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContribuyenteOutputDTO {
    private Long idContribuyente;
    private String nombre;
    private String apellido;
}

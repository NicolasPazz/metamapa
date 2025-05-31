package ar.utn.ba.ddsi.dinamica.domain.dto.input;


import lombok.Data;

import java.util.List;

@Data
public class HechoInputContribuyenteDTO {
    private Long idContribuyente;
    private String titulo;
    private String descripcion;
    private String categoria;
    private double latitud;
    private double longitud;
    private String fechaDeAcontecimiento;
    private List<String> multimedia;

    //las etiquetas las agregan los admin por lo que deberiamos tener otro input con id de hecho id de admin y set de etiquetas, pero al iniciar un hecho no viene con etiquetas
}

package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import lombok.Data;

@Data
public class ContenidoMultimediaOutputDTO {
        String pathArchivo;
        String tipoContenido;

    public ContenidoMultimediaOutputDTO(String pathArchivo, String tipoContenido) {
        this.pathArchivo = pathArchivo;
        this.tipoContenido = tipoContenido;
    }

}

// java
package ar.utn.ba.ddsi.frontend.service;

import com.metamapa.dtos.SolicitudHechoInputContribuyenteDTO;
import com.metamapa.dtos.SolicitudModificarUserInputDTO;

public interface ContribuyenteService {
    void guardarHecho(SolicitudHechoInputContribuyenteDTO dto);
    SolicitudModificarUserInputDTO obtenerHechoParaEdicion(Long id);
    void actualizarHecho(Long id, SolicitudModificarUserInputDTO dto);
}
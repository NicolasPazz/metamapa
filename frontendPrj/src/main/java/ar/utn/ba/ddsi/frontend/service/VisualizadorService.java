// java
package ar.utn.ba.ddsi.frontend.service;

import com.metamapa.dtos.HechoOutputDTO;

public interface VisualizadorService {
    HechoOutputDTO obtenerHechoPorId(Long id);
    // List\<ColeccionDTO\> obtenerColecciones();
    // List\<HechoOutputDTO\> obtenerHechosFiltrados(...);
}
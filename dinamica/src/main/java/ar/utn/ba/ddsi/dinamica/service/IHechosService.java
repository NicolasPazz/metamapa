package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.HechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.HechoModificarInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;

import java.util.List;

public interface IHechosService {
  List<HechoOutputDTO> buscarTodos();
  HechoOutputDTO crearPorContribuyente(HechoInputContribuyenteDTO input);
  HechoOutputDTO modificarHecho(Long id, HechoModificarInputDTO input);

}

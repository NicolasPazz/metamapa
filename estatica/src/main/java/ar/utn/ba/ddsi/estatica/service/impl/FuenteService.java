package ar.utn.ba.ddsi.estatica.service.impl;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FuenteService implements IFuenteService {
  private final IHechosRepository hechosRepository;

  public FuenteService(IHechosRepository hechosRepository) {
    this.hechosRepository = hechosRepository;
  }

  private LocalDateTime ultimaLlamada = null;

  @Override
  public List<HechoOutputDTO> getAllHechosNuevos() {
    List<Hecho> hechos = this.hechosRepository.findAll();
    List<HechoOutputDTO> nuevos = hechos.stream()
        .filter(h -> {
          LocalDateTime ultimaTransicion = h.getTransiciones().get(h.getTransiciones().size() - 1).getFechaTransicion();

          return ultimaLlamada == null || ultimaTransicion.isAfter(ultimaLlamada);
        })
        .map(this::hechoOutputDTO)
        .toList();

    // Actualizar la ultima llamada
    ultimaLlamada = LocalDateTime.now();

    return nuevos;
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho){
     return new HechoOutputDTO(
        hecho.getId(),
        hecho.getTitulo(),
        hecho.getDescripcion(),
        hecho.getNombreCategoria(),
        hecho.getLatitud(),
        hecho.getLongitud(),
        hecho.getFechaDeAcontecimiento(),
        hecho.getFechaDeCarga(),
        hecho.getTransiciones().get(hecho.getTransiciones().size() - 1).getFechaTransicion()
        );
  }
}
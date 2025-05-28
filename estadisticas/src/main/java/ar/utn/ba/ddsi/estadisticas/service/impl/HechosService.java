package ar.utn.ba.ddsi.estadisticas.service.impl;

import ar.utn.ba.ddsi.estadisticas.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.estadisticas.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estadisticas.domain.hecho.Categoria;
import ar.utn.ba.ddsi.estadisticas.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.estadisticas.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.estadisticas.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.ICategoriasRepository;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.IEtiquetasRepository;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.IMultimediasRepository;
import ar.utn.ba.ddsi.estadisticas.domain.user.Contribuyente;
import ar.utn.ba.ddsi.estadisticas.service.IHechosService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HechosService implements IHechosService {
  @Autowired
  private IHechosRepository hechosRespository;
  @Autowired
  private IEtiquetasRepository etiquetasRepository;
  @Autowired
  private IContribuyentesRepository contribuyentesRepository;
  @Autowired
  private ICategoriasRepository categoriasRepository;
  @Autowired
  private IMultimediasRepository multimediasRepository;

  @Override
  public List<HechoOutputDTO> buscarTodos() {
    List<Hecho> hechos = this.hechosRespository.findAll();
    return hechos
        .stream()
        .map(this::hechoOutputDTO)
        .toList();
  }

  @Override
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO) {
    Set<Etiqueta> etiquetas = etiquetasRepository.findAllById(hechoInputDTO.getIdEtiquetas());
    if(etiquetas == null) {
      // TODO: manejar el caso en que no se encuentren etiquetas
    }
    Contribuyente contribuyente = contribuyentesRepository.findById(hechoInputDTO.getIdContribuyente());
    if(contribuyente == null) {
      // TODO: manejar el caso en que no se encuentre el contribuyente
    }
    Categoria categoria = categoriasRepository.findById(hechoInputDTO.getIdCategoria());
    if(categoria == null) {
      //TODO: manejar el caso en que no se encuentre la categoria
    }
    List<ContenidoMultimedia> multimedia = multimediasRepository.findAllById(hechoInputDTO.getIdMultimedia());
    if(multimedia == null) {
      //TODO: manejar el caso en que no se encuentren los multimedia
    }

    Hecho nuevoHecho = new Hecho(
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria,
        LocalDate.parse(hechoInputDTO.getFechaDeAcontecimiento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        hechoInputDTO.getLatitud(),
        hechoInputDTO.getLongitud(),
        multimedia,
        etiquetas,
        contribuyente
    );

    this.hechosRespository.save(nuevoHecho);

    return this.hechoOutputDTO(nuevoHecho);
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    // TODO: eliminar lo que no sea necesario
    var dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setOrigen(hecho.getOrigen().name().toLowerCase());
    dto.setEstado(hecho.getEstado().name().toLowerCase());
    dto.setIdContribuyente(hecho.getContribuyente().getId());
    dto.setIdEtiquetas(hecho.getIdEtiquetas());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setIdCategoria(hecho.getCategoria().getId());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().toString());
    dto.setFechaDeCarga(hecho.getFechaDeCarga().toString());
    dto.setMultimediaPaths(hecho.getMultimediaPaths());
    return dto;
  }
}
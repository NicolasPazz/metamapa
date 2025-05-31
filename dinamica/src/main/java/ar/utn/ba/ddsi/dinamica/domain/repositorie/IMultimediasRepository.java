package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.Etiqueta;

import java.util.List;

public interface IMultimediasRepository {
    public ContenidoMultimedia findById(Long id);
    public List<ContenidoMultimedia> findAll();
    public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
    public void save(ContenidoMultimedia multimedia);
    public void delete(ContenidoMultimedia multimedia);
}

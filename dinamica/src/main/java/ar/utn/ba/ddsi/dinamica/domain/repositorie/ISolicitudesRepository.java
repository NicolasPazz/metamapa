package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import java.util.List;

public interface ISolicitudesRepository {
    public SolicitudHecho findById(Long id);
    public List<SolicitudHecho> findAll();
    public Long save(SolicitudHecho solicitudHecho);
    public void update(SolicitudHecho solicitudHecho);
    public void delete(SolicitudHecho solicitudHecho);
}

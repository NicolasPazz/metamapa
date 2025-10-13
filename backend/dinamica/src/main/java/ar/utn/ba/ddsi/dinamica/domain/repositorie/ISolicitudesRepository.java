package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudesRepository extends JpaRepository<SolicitudHecho, Long> {
    /*public SolicitudHecho findById(Long id);
    public List<SolicitudHecho> findAll();
    public Long save(SolicitudHecho solicitudHecho);
    public void update(SolicitudHecho solicitudHecho);
    public void delete(SolicitudHecho solicitudHecho);*/
}

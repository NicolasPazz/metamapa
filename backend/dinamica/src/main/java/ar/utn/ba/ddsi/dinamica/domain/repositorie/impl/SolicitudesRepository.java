package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {

    private List<SolicitudHecho> solicitudes = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public SolicitudHecho findById(Long id) {
        return this.solicitudes.stream()
                .filter(s->s.getIdSolicitud().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SolicitudHecho> findAll() {
        return this.solicitudes;
    }

    @Override
    public Long save(SolicitudHecho solicitudHecho) {
        Long nuevoId = idGenerator.getAndIncrement();
        solicitudHecho.setIdSolicitud(nuevoId);
        this.solicitudes.add(solicitudHecho);
        return nuevoId;
    }

    @Override
    public void delete(SolicitudHecho solicitudHecho) {
        this.solicitudes.remove(solicitudHecho);
    }

    @Override
    public void update(SolicitudHecho solicitudHecho) {
        for (int i = 0; i < solicitudes.size(); i++) {
            if (solicitudes.get(i).getIdSolicitud().equals(solicitudHecho.getIdSolicitud())) {
                solicitudes.set(i, solicitudHecho);
                return;
            }
        }
    }

}

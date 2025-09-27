package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.user.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IContribuyentesRepository extends JpaRepository<Contribuyente, Long> {
    /*public Contribuyente findById(Long id);
    public List<Contribuyente> findAll();
    public void save(Contribuyente contribuyente);
    public void delete(Contribuyente contribuyente);*/
}

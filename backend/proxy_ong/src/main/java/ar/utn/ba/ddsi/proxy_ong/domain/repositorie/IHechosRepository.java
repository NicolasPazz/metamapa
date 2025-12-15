package ar.utn.ba.ddsi.proxy_ong.domain.repositorie;

import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
}
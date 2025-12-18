package ar.utn.ba.ddsi.repositories;

import ar.utn.ba.ddsi.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuariosRepository extends JpaRepository<Usuario, Long> {
}

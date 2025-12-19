package ar.utn.ba.ddsi.users.repositories;

import ar.utn.ba.ddsi.users.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuariosRepository extends JpaRepository<Usuario, Long> {
}

package ar.utn.ba.ddsi.estatica.repositorie;

import ar.utn.ba.ddsi.estatica.domain.ContenidoCsv;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IContenidoCsvRepository extends JpaRepository<ContenidoCsv, String> {

  @Query("select c.nombre from ContenidoCsv c order by c.nombre")
  List<String> findAllNombres();
}

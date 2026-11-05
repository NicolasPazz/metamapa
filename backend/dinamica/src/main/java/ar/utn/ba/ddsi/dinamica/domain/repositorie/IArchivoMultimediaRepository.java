package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.multimedia.ArchivoMultimedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IArchivoMultimediaRepository extends JpaRepository<ArchivoMultimedia, String> {
}

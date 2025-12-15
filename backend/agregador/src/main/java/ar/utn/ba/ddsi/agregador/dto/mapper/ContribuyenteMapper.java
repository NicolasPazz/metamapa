package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.dto.ContribuyenteDTO;
import ar.utn.ba.ddsi.agregador.domain.user.Contribuyente;
import ar.utn.ba.ddsi.agregador.service.ContribuyenteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ContribuyenteMapper {
  private final ContribuyenteService contribuyenteService;

  public ContribuyenteDTO toDTO(Long contribuyenteId) {
    if (contribuyenteId == null) return null;

    Contribuyente c = contribuyenteService.findById(contribuyenteId);
    return this.toDTO(c);
  }

  public ContribuyenteDTO toDTO(Contribuyente c) {
    if (c == null) return null;

    return new ContribuyenteDTO(
        c.getId(),
        c.getNombre(),
        c.getApellido(),
        c.getFechaDeNacimiento()
    );
  }

  public String fromDTO(Contribuyente c) {
    return c != null ? c.getNombre() + " " + c.getApellido() : null;
  }
}

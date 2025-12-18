package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.user.Contribuyente;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContribuyenteService {
  @Value("${users.base-url}")
  private String baseUrl;

  public Contribuyente findById(Long id) {
    return new Contribuyente("Nombre", "Apellido", LocalDate.of(2025, 1, 1));
  }
}

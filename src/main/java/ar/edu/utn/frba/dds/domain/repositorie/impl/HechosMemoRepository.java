package ar.edu.utn.frba.dds.domain.repositorie.impl;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.repositorie.IHechosRespository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class HechosMemoRepository implements IHechosRespository {
  private List<Hecho> hechos;
}

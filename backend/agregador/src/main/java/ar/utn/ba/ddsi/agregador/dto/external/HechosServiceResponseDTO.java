package ar.utn.ba.ddsi.agregador.dto.external;

import lombok.Data;

import java.util.List;

@Data
public class HechosServiceResponseDTO {
    private List<HechosServiceDTO> hechos;
}

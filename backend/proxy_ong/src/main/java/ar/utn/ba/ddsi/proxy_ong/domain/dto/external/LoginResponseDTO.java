package ar.utn.ba.ddsi.proxy_ong.domain.dto.external;

import lombok.Data;

@Data
public class LoginResponseDTO {
  private boolean error;
  private String message;
  private LoginData data;
}

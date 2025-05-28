package ar.utn.ba.ddsi.fuenteproxy.domain.dto.external;

import lombok.Data;

@Data
public class LoginResponseDTO {
  private boolean error;
  private String message;
  private LoginData data;

  @Data
  public static class LoginData {
    private String access_token;
    private String token_type;
  }

}

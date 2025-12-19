package ar.utn.ba.ddsi.proxy_ong.domain.dto.external;

import lombok.Data;

@Data
public class LoginData {
  private String access_token;
  private String token_type;
}


package ar.utn.ba.ddsi.users.dto;

import ar.utn.ba.ddsi.users.domain.Permiso;
import ar.utn.ba.ddsi.users.domain.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesPermissionsDTO {
    private Long userId;
    private String username;
    private Rol rol;
    private List<Permiso> permisos;
}

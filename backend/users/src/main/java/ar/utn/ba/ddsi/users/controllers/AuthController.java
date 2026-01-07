package ar.utn.ba.ddsi.users.controllers;

import ar.utn.ba.ddsi.users.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.users.dto.RefreshRequest;
import ar.utn.ba.ddsi.users.dto.TokenResponse;
import ar.utn.ba.ddsi.users.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.users.services.LoginService;
import ar.utn.ba.ddsi.users.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO> loginApi(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        log.info("[AuthController] - POST - /api/auth - username: {}, password: [PROTEGIDO]", username);
        try {
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            loginService.autenticarUsuario(username, password);
            
            String accessToken = loginService.generarAccessToken(username);
            String refreshToken = loginService.generarRefreshToken(username);
            
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        log.info("[AuthController] - POST - /api/auth/refresh - refreshToken: [PROTEGIDO]");
        try {
            String username = JwtUtil.validarToken(request.getRefreshToken());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(request.getRefreshToken())
                    .getBody();

            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.badRequest().build();
            }

            String newAccessToken = JwtUtil.generarAccessToken(username);
            TokenResponse response = new TokenResponse(newAccessToken, request.getRefreshToken());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");
        String nombre = userData.get("nombre");
        String apellido = userData.get("apellido");
        log.info("[AuthController] - POST - /api/auth/register - username: {}, password: [PROTEGIDO], nombre: {}, apellido: {}", username, nombre, apellido);
        try {
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty() ||
                nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username, password y nombre son obligatorios"));
            }
            
            loginService.registrarUsuario(username, password, nombre, apellido);
            
            return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente"));
            
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("ya está en uso")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ese nombre de usuario ya está en uso"));
            }
            return ResponseEntity.badRequest()
                .body(Map.of("error", errorMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/user/roles-permisos")
    public ResponseEntity<UserRolesPermissionsDTO> getUserRolesAndPermissions(Authentication authentication) {
        log.info("[AuthController] - GET - /api/auth/user/roles-permisos - Username: {}", authentication != null ? authentication.getName() : "null");
        try {
            String username = authentication.getName();
            UserRolesPermissionsDTO response = loginService.obtenerRolesYPermisosUsuario(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

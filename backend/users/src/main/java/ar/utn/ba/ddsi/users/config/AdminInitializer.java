package ar.utn.ba.ddsi.users.config;

import ar.utn.ba.ddsi.users.domain.Rol;
import ar.utn.ba.ddsi.users.domain.Usuario;
import ar.utn.ba.ddsi.users.repositories.IUsuariosRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final IUsuariosRepository usuariosRepository;
    private final String adminUsername;
    private final String adminPassword;
    private final String contribUsername;
    private final String contribPassword;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminInitializer(
            IUsuariosRepository usuariosRepository,
            @Value("${admin.username:}") String adminUsername,
            @Value("${admin.password:}") String adminPassword,
            @Value("${seed.contrib.username:}") String contribUsername,
            @Value("${seed.contrib.password:}") String contribPassword) {
        this.usuariosRepository = usuariosRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.contribUsername = contribUsername;
        this.contribPassword = contribPassword;
    }

    @PostConstruct
    public void init() {
        try {
            // Admin
            if (adminUsername == null || adminUsername.isEmpty() ||
                adminPassword == null || adminPassword.isEmpty()) {
                log.warn("admin.username y/o admin.password no configuradas. Saltando inicialización de admin.");
            } else {
                upsertUsuario(adminUsername, adminPassword, "Nicolas", Rol.ADMIN);
            }

            // Contribuyente
            if (contribUsername == null || contribUsername.isEmpty() ||
                contribPassword == null || contribPassword.isEmpty()) {
                log.warn("seed.contrib.username y/o seed.contrib.password no configuradas. Saltando inicialización de contribuyente.");
            } else {
                upsertUsuario(contribUsername, contribPassword, "Lauri", Rol.CONTRIBUYENTE);
            }
        } catch (Exception e) {
            log.error("Error al inicializar usuario admin", e);
        }
    }

    private void upsertUsuario(String username, String rawPassword, String nombre, Rol rol) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByUsername(username);

        Usuario usuario = usuarioOpt.orElseGet(Usuario::new);
        boolean isNew = usuarioOpt.isEmpty();

        usuario.setUsername(username);
        usuario.setNombre(nombre);
        usuario.setApellido(null);

        List<Rol> roles = new ArrayList<>();
        roles.add(rol);
        usuario.setRoles(roles);

        // En dev: asegurar credenciales conocidas. No loguear contraseñas.
        usuario.setPassword(passwordEncoder.encode(rawPassword));

        usuariosRepository.save(usuario);

        if (isNew) {
            log.info("Usuario seed creado: username={}, rol={}", username, rol);
        } else {
            log.info("Usuario seed actualizado: username={}, rol={}", username, rol);
        }
    }
}

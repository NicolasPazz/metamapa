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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminInitializer(
            IUsuariosRepository usuariosRepository,
            @Value("${admin.username:}") String adminUsername,
            @Value("${admin.password:}") String adminPassword) {
        this.usuariosRepository = usuariosRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @PostConstruct
    public void init() {
        try {
            // Solo crear admin si las credenciales están configuradas
            if (adminUsername == null || adminUsername.isEmpty() || 
                adminPassword == null || adminPassword.isEmpty()) {
                log.warn("admin.username y/o admin.password no configuradas. Saltando inicialización de admin.");
                return;
            }
            
            Optional<Usuario> adminOpt = usuariosRepository.findByUsername(adminUsername);
            
            if (adminOpt.isEmpty()) {
                log.info("No se encontró usuario admin. Creando usuario admin con username: {}", adminUsername);
                
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido(null);
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                
                List<Rol> roles = new ArrayList<>();
                roles.add(Rol.ADMIN);
                admin.setRoles(roles);
                
                usuariosRepository.save(admin);
                log.info("Usuario admin creado exitosamente con username: {}", adminUsername);
            } else {
                log.info("Usuario admin ya existe con username: {}", adminUsername);
            }
        } catch (Exception e) {
            log.error("Error al inicializar usuario admin", e);
        }
    }
}

package ar.utn.ba.ddsi.users.services;

import ar.utn.ba.ddsi.users.domain.Permiso;
import ar.utn.ba.ddsi.users.domain.Rol;
import ar.utn.ba.ddsi.users.domain.Usuario;
import ar.utn.ba.ddsi.users.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.users.repositories.IUsuariosRepository;
import ar.utn.ba.ddsi.users.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoginService {
    
    private final IUsuariosRepository usuariosRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public LoginService(IUsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    public Usuario autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Username no encontrado: " + username);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return usuario;
    }
    
    public String generarAccessToken(String username) {
        return JwtUtil.generarAccessToken(username);
    }
    
    public String generarRefreshToken(String username) {
        return JwtUtil.generarRefreshToken(username);
    }
    
    public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Username no encontrado: " + username);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        Rol rolPrincipal = usuario.getRoles() != null && !usuario.getRoles().isEmpty() 
            ? usuario.getRoles().get(0) 
            : Rol.CONTRIBUYENTE;
        
        List<Permiso> permisos = obtenerPermisosPorRol(rolPrincipal);
        
        return UserRolesPermissionsDTO.builder()
                .userId(usuario.getId())
                .username(usuario.getUsername())
                .rol(rolPrincipal)
                .permisos(permisos)
                .build();
    }
    
    public Usuario registrarUsuario(String username, String password, String nombre, String apellido) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("El username no puede estar vacío");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("La contraseña no puede estar vacía");
        }
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre no puede estar vacío");
        }
        
        Optional<Usuario> usuarioExistente = usuariosRepository.findByUsername(username.trim());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Ese nombre de usuario ya está en uso");
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username.trim());
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setNombre(nombre.trim());
        nuevoUsuario.setApellido(apellido != null ? apellido.trim() : null);
        
        List<Rol> roles = new ArrayList<>();
        roles.add(Rol.CONTRIBUYENTE);
        nuevoUsuario.setRoles(roles);
        
        return usuariosRepository.save(nuevoUsuario);
    }
    
    private List<Permiso> obtenerPermisosPorRol(Rol rol) {
        List<Permiso> permisos = new ArrayList<>();
        
        switch (rol) {
            case ADMIN:
                permisos.add(Permiso.MANEJAR_COLECCIONES);
                permisos.add(Permiso.MANEJAR_SOLICITUDES);
                permisos.add(Permiso.CREAR_HECHO);
                permisos.add(Permiso.SOLICITAR_MODIFICACION);
                break;
            case CONTRIBUYENTE:
                permisos.add(Permiso.CREAR_HECHO);
                permisos.add(Permiso.SOLICITAR_MODIFICACION);
                break;
            default:
                break;
        }
        
        return permisos;
    }
}

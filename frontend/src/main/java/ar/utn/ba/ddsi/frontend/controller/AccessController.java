// Java
package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.services.MetaMapaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AccessController {

    @Autowired
    private MetaMapaApiService metaMapaApiService;

    // Registro de Contribuyentes
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        return "access/register"; // plantilla: 'src/main/resources/templates/registro.html'
    }

    @PostMapping("/registro")
    public Object procesarRegistro(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam(required = false) String apellido,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            metaMapaApiService.register(username, password, nombre, apellido);
            
            // Si es una petición AJAX, devolver JSON
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "true");
                response.put("message", "Registro exitoso");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
            // Si no es AJAX, redirigir normalmente
            redirectAttributes.addFlashAttribute("success", "true");
            return "redirect:/login";
        } catch (RuntimeException e) {
            // Si es una petición AJAX, devolver JSON con error
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "false");
                response.put("error", e.getMessage());
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
            // Si no es AJAX, redirigir normalmente
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        } catch (Exception e) {
            // Si es una petición AJAX, devolver JSON con error
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "false");
                response.put("error", "Error al registrar usuario. Por favor, intente nuevamente.");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
            // Si no es AJAX, redirigir normalmente
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario. Por favor, intente nuevamente.");
            return "redirect:/registro";
        }
    }
    
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equals(requestedWith) || 
               (accept != null && accept.contains("application/json"));
    }

    // Inicio de Sesión
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "access/login"; // plantilla: 'src/main/resources/templates/login.html'
    }

    // Página de error 403
    @GetMapping("/403")
    public String error403(Model model) {
        return "error/403";
    }
}

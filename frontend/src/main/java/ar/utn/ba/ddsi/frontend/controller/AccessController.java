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
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class AccessController {

    @Autowired
    private MetaMapaApiService metaMapaApiService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        log.info("[AccessController] - GET - /registro");
        return "access/register";
    }

    @PostMapping("/registro")
    public Object procesarRegistro(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam(required = false) String apellido,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        log.info("[AccessController] - POST - /registro - username: {}, password: [PROTEGIDO], nombre: {}, apellido: {}", username, nombre, apellido);
        try {
            metaMapaApiService.register(username, password, nombre, apellido);
            
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "true");
                response.put("message", "Registro exitoso");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
            redirectAttributes.addFlashAttribute("success", "true");
            return "redirect:/login";
        } catch (RuntimeException e) {
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "false");
                response.put("error", e.getMessage());
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        } catch (Exception e) {
            if (isAjaxRequest(request)) {
                Map<String, String> response = new HashMap<>();
                response.put("success", "false");
                response.put("error", "Error al registrar usuario. Por favor, intente nuevamente.");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
            }
            
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

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        log.info("[AccessController] - GET - /login");
        return "access/login";
    }

    @GetMapping("/403")
    public String error403(Model model) {
        log.info("[AccessController] - GET - /403");
        return "error/403";
    }

    @GetMapping("/404")
    public String error404(Model model) {
        log.info("[AccessController] - GET - /404");
        return "error/404";
    }

    @GetMapping("/500")
    public String error500(Model model) {
        log.info("[AccessController] - GET - /500");
        return "error/500";
    }

    @GetMapping("/error")
    public String error(Model model) {
        log.info("[AccessController] - GET - /error");
        return "error/error";
    }
}

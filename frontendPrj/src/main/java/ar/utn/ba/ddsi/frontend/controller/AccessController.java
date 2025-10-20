// Java
package ar.utn.ba.ddsi.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessController {

    // Visualización de Hechos en Mapa Interactivo
    @GetMapping("/hechos/mapa")
    public String mostrarMapaHechos(Model model) {
        // model.addAttribute("hechos", service.obtenerHechosParaMapa());
        return "mapa/mapa"; // plantilla: ‘src/main/resources/templates/mapa-hechos.html’
    }

    // Registro de Contribuyentes
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        return "access/register"; // plantilla: ‘src/main/resources/templates/registro.html’
    }

    // Inicio de Sesión
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "access/login"; // plantilla: ‘src/main/resources/templates/login.html’
    }
}
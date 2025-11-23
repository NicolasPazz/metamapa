// Java
package ar.utn.ba.ddsi.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessController {


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
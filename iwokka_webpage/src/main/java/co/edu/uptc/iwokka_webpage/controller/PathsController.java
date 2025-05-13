package co.edu.uptc.iwokka_webpage.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PathsController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("serverTime", LocalDateTime.now());
        return "index"; // Nombre de la plantilla (sin .html)
    }

    @GetMapping("/clientes")
    public String clientes() {
        return "clientes"; // clientes.html
    }

    @GetMapping("/tiendas")
    public String tiendas() {
        return "tiendas"; // tiendas.html
    }
}

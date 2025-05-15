package co.edu.uptc.iwokka_webpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PathsController {

    @GetMapping("/")
    public String home(Model model) {
        return "login"; 
    }

    @GetMapping("/register")
    public String clientes() {
        return "register"; 
    }

    @GetMapping("/index")
    public String tiendas() {
        return "index";
    }
}

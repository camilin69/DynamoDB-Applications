package co.edu.uptc.iwokka_webpage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private final ClientService clientService;

    public ClientController() {
        this.clientService = new ClientService();
    }

    @PostMapping("/save")
    public Client save(@RequestBody Client client) {
        return clientService.save(client);
    }

    @GetMapping("/findById")
    public Client findById(@RequestParam String id) {
        return clientService.findById(id);
    }

    @GetMapping("/findByEmail")
    public Client findByEmail(@RequestParam String email) {
        return clientService.findByEmail(email);
    }

    @GetMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return clientService.login(email, password);  
    }

    @GetMapping("/findAll")
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @DeleteMapping("/deleteById")
    public void deleteClient(@PathVariable String id) {
        clientService.deleteById(id);
    }
}

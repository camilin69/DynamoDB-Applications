package co.edu.uptc.iwokka_webpage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.repository.ClientRepository;

@Service
public class ClientService {
    @Autowired
    private final ClientRepository clientRepository;

    public ClientService() {
        this.clientRepository = new ClientRepository();
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client fingById(String id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void deleteById(String id) {
        clientRepository.deleteById(id);
    }
}

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

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client getClientById(String id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public void deleteClient(String id) {
        clientRepository.delete(id);
    }
}

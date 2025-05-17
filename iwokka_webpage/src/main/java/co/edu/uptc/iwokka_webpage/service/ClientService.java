package co.edu.uptc.iwokka_webpage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.repository.ClientRepository;

@Service
public class ClientService {
    public static final String OK_CLIENT_FOUND = "OK: CLIENT FOUND";
    public static final String ERROR_CLIENT_NOT_FOUND = "ERROR: CLIENT NOT FOUND";
    public static final String ERROR_PASSWORD_MISMATCH= "ERROR: PASSWORD MISMATCH FOR EMAIL GIVEN";


    @Autowired
    private final ClientRepository clientRepository;

    public ClientService() {
        this.clientRepository = new ClientRepository();
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client findById(String id) {
        return clientRepository.findById(id);
    }

    public Client findByEmail (String email) {
        return clientRepository.findByEmail(email);
    }

    public Client login (String email, String password) {
        Client client = clientRepository.findByEmail(email);
        if(client != null) {
            return client.getPassword().equals(password) ? client : null;
        } 
        return null;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void deleteById(String id) {
        clientRepository.deleteById(id);
    }
}

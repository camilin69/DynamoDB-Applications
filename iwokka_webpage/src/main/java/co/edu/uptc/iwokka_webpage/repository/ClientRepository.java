package co.edu.uptc.iwokka_webpage.repository;
import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.model.Client;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import java.util.List;

@Repository
public class ClientRepository {
    private final DynamoDbTable<Client> clientTable;

    public ClientRepository(DynamoDbEnhancedClient dynamoClient) {
        this.clientTable = dynamoClient.table("Clients", TableSchema.fromBean(Client.class));
    }

    public Client save(Client client) {
        clientTable.putItem(client);
        return client;
    }

    public Client findById(String id) {
        return clientTable.getItem(Key.builder().partitionValue(id).build());
    }

    public List<Client> findAll() {
        return clientTable.scan().items().stream().toList();
    }

    public void delete(String id) {
        clientTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}

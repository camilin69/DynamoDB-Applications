package co.edu.uptc.iwokka_webpage.repository;
import java.util.List;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.config.DynamoDBConfig;
import co.edu.uptc.iwokka_webpage.model.Client;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class ClientRepository {
    private final DynamoDbTable<Client> clientTable;

    public ClientRepository() {
        this.clientTable = DynamoDBConfig.dynamoDbEnhancedClient().table("Clients", TableSchema.fromBean(Client.class));
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

    public void deleteById(String id) {
        clientTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}

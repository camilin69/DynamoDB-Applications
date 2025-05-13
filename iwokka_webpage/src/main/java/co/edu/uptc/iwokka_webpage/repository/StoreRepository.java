package co.edu.uptc.iwokka_webpage.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.model.Store;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class StoreRepository {
    private final DynamoDbTable<Store> storeTable;

    public StoreRepository(DynamoDbEnhancedClient dynamoClient) {
        this.storeTable = dynamoClient.table("Stores", TableSchema.fromBean(Store.class));
    }

    public Store save(Store store) {
        storeTable.putItem(store);
        return store;
    }

    public Store findByCategoryAndLabel(String category, String label) {
        return storeTable.getItem(Key.builder()
            .partitionValue(category)
            .sortValue(label)
            .build());
    }

    public List<Store> findAll() {
        return storeTable.scan().items().stream().toList();
    }

    public void delete(String category, String label) {
        storeTable.deleteItem(Key.builder()
            .partitionValue(category)
            .sortValue(label)
            .build());
    }
}

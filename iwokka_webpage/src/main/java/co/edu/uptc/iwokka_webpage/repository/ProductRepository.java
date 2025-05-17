package co.edu.uptc.iwokka_webpage.repository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.config.DynamoDBConfig;
import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Repository
public class ProductRepository {
    private final DynamoDbTable<Product> productTable;

    public ProductRepository() {
        this.productTable = DynamoDBConfig.dynamoDbEnhancedClient().table("Products", TableSchema.fromBean(Product.class));
    }

    public Product save(Product product) {
        productTable.putItem(product);
        return product;
    }

    public List<Product> findAllByLabel(String label) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(label)
                .build()))
            .build();

        return productTable.query(queryRequest)
            .items()
            .stream()
            .toList();
    }

    public Set<String> findAllUniqueLabels() {
        Set<String> uniqueLabels = new HashSet<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Products")  
                .exclusiveStartKey(lastEvaluatedKey)
                .projectionExpression("label")  
                .build();

            ScanResponse response = DynamoDBConfig.dynamoDbClient().scan(scanRequest);

            response.items().forEach(item -> {
                if (item.containsKey("label")) {
                    uniqueLabels.add(item.get("label").s());
                }
            });

            lastEvaluatedKey = response.lastEvaluatedKey();

        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return uniqueLabels;
    }

    public List<Product> findAll() {
        return productTable.scan().items().stream().toList();
    }

    
    public void delete(String label) {
        productTable.deleteItem(Key.builder().partitionValue(label).build());
    }
}

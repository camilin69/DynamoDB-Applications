package co.edu.uptc.iwokka_webpage.repository;
import java.util.List;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.config.DynamoDBConfig;
import co.edu.uptc.iwokka_webpage.model.Product;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

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

    public Product findByLabel(String label) {
        return productTable.getItem(Key.builder().partitionValue(label).build());
    }

    public List<Product> findAll() {
        return productTable.scan().items().stream().toList();
    }

    public void delete(String label) {
        productTable.deleteItem(Key.builder().partitionValue(label).build());
    }
}

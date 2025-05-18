package co.edu.uptc.iwokka_webpage.repository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.config.DynamoDBConfig;
import co.edu.uptc.iwokka_webpage.model.Product;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
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

    public Product updateProduct(String oldLabel, String oldName, Product newProduct) {
        try {
            if (newProduct.getLabel() == null || newProduct.getLabel().isEmpty() ||
                newProduct.getName() == null || newProduct.getName().isEmpty()) {
                throw new IllegalArgumentException("Product label and name cannot be null or empty");
            }
    
            Product existingProduct = productTable.getItem(Key.builder()
                .partitionValue(oldLabel)
                .sortValue(oldName)
                .build());
            
            if (existingProduct == null) {
                throw new IllegalArgumentException("Product not found");
            }
    
            // if keys don't change
            if (oldLabel.equals(newProduct.getLabel()) && oldName.equals(newProduct.getName())) {
                if (newProduct.getDescription() != null) {
                    existingProduct.setDescription(newProduct.getDescription());
                }
                if (newProduct.getPrice() >= 0) {
                    existingProduct.setPrice(newProduct.getPrice());
                }
                productTable.updateItem(existingProduct);
                return existingProduct;
            }
            
            // if keys change delete and update new
            Product updatedProduct = new Product();
            updatedProduct.setLabel(newProduct.getLabel());
            updatedProduct.setName(newProduct.getName());
            updatedProduct.setDescription(
                newProduct.getDescription() != null ? 
                newProduct.getDescription() : existingProduct.getDescription());
            updatedProduct.setPrice(
                newProduct.getPrice() >= 0 ? 
                newProduct.getPrice() : existingProduct.getPrice());
    
            // Transaction for atomicity
            DynamoDBConfig.dynamoDbEnhancedClient().transactWriteItems(builder -> builder
                .addPutItem(productTable, updatedProduct)
                .addDeleteItem(productTable, Key.builder()
                    .partitionValue(oldLabel)
                    .sortValue(oldName)
                    .build())
            );
            
            return updatedProduct;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }
    
    public void delete(String label, String name) {
        productTable.deleteItem(Key.builder()
            .partitionValue(label)
            .sortValue(name)
            .build());
    }
}

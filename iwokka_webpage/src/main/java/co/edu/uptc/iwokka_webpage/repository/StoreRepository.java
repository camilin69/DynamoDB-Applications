package co.edu.uptc.iwokka_webpage.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import co.edu.uptc.iwokka_webpage.config.DynamoDBConfig;
import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Repository
public class StoreRepository {
    private final DynamoDbTable<Store> storeTable;
    private final DynamoDbIndex<Store> categoryIndex;
    private final ProductRepository productRepository;

    public StoreRepository() {
        this.storeTable = DynamoDBConfig.dynamoDbEnhancedClient().table("Stores", TableSchema.fromBean(Store.class));
        this.categoryIndex = storeTable.index("category-index");
        productRepository = new ProductRepository();
    }

    public Store save(Store store) {
        storeTable.putItem(store);
        return store;
    }

    public Set<String> findAllUniqueCategories() {
        Set<String> uniqueCategories = new HashSet<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .exclusiveStartKey(lastEvaluatedKey)
                .attributesToProject("category") 
                .build();

            // Get pages from scan
            SdkIterable<Page<Store>> pages = categoryIndex.scan(scanRequest);

            
            for (Page<Store> page : pages) {
                // Get categories from the page
                page.items().stream()
                    .map(Store::getCategory)
                    .forEach(uniqueCategories::add);

                lastEvaluatedKey = page.lastEvaluatedKey();
            }

        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return Collections.unmodifiableSet(uniqueCategories);
    }

    public List<Store> findAllByCategory(String category) {
        return categoryIndex.query(QueryConditional.keyEqualTo(
            Key.builder().partitionValue(category).build()
        ))
        .stream()
        .flatMap(page -> page.items().stream()) 
        .toList(); 
    }

    public Store findByCategoryAndLabel(String category, String label) {
        return storeTable.getItem(Key.builder()
            .partitionValue(category)
            .sortValue(label)
            .build());
    }

    public Store updateStore(String oldCategory, String oldLabel, String newCategory, String newLabel) {        
        Store store = findByCategoryAndLabel(oldCategory, oldLabel);
        if (store == null) {
            throw new IllegalArgumentException("Store does not exist");
        }
        
        Store newStore = new Store();
        newStore.setCategory(newCategory);
        newStore.setLabel(newLabel);
        newStore.setProducts(store.getProducts());
        //newStore.setClients(store.getClients());
        
        save(newStore);
        
        delete(oldCategory, oldLabel);
        
        return newStore;
    }

    //NEVER USED
    public void updateClients(String category, String label, Client client) {

        DynamoDbClient dynamoDbClient = DynamoDBConfig.dynamoDbClient();
        
        Store store = findByCategoryAndLabel(category, label);
        if (store == null) {
            throw new IllegalArgumentException("Store does not exist");
        }

        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("category", AttributeValue.builder().s(category).build());
        key.put("label", AttributeValue.builder().s(label).build());
        
        Map<String, AttributeValue> clientMap = new HashMap<>();
        if (client != null) {
            clientMap.put("id", AttributeValue.builder().s(client.getId()).build());
            clientMap.put("name", AttributeValue.builder().s(client.getName()).build());
            clientMap.put("email", AttributeValue.builder().s(client.getEmail()).build());
            clientMap.put("password", AttributeValue.builder().s(client.getPassword()).build());
            clientMap.put("role", AttributeValue.builder().s(client.getRole()).build());
            
        }
        
        AttributeValue clientListValue = AttributeValue.builder()
                .l(List.of(AttributeValue.builder().m(clientMap).build()))
                .build();
        
        String updateExpression = "SET clients = list_append(if_not_exists(clients, :empty_list), :newClient)";
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newClient", clientListValue);
        expressionAttributeValues.put(":empty_list", AttributeValue.builder().l(new ArrayList<>()).build());
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("Stores")
                .key(key)
                .updateExpression(updateExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        
        try {
            dynamoDbClient.updateItem(request);
            System.out.println("Client added to Store " + category + " - " + label);
        } catch (ResourceNotFoundException e) {
            System.err.println("Table does not exist: " + e.getMessage());
            throw e;
        } catch (DynamoDbException e) {
            System.err.println("Error updating clients: " + e.getMessage());
            throw e;
        }
    }
    

    //Adds a product
    public Store updateProducts(String category, String label, Product product) {

        DynamoDbClient dynamoDbClient = DynamoDBConfig.dynamoDbClient();
        
        Store store = findByCategoryAndLabel(category, label);
        if (store == null) {
            throw new IllegalArgumentException("Store does not exist");
        }
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("category", AttributeValue.builder().s(category).build());
        key.put("label", AttributeValue.builder().s(label).build());
        
        Map<String, AttributeValue> productMap = new HashMap<>();
        if (product != null) {
            productMap.put("label", AttributeValue.builder().s(product.getLabel()).build());
            productMap.put("name", AttributeValue.builder().s(product.getName()).build());
            productMap.put("description", AttributeValue.builder().s(product.getDescription()).build());
            productMap.put("price", AttributeValue.builder().s(String.valueOf(product.getPrice())).build());

        }
        
        AttributeValue productListValue = AttributeValue.builder()
                .l(List.of(AttributeValue.builder().m(productMap).build()))
                .build();
        
        String updateExpression = "SET products = list_append(if_not_exists(products, :empty_list), :newProduct)";
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newProduct", productListValue);
        expressionAttributeValues.put(":empty_list", AttributeValue.builder().l(new ArrayList<>()).build());
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("Stores")
                .key(key)
                .updateExpression(updateExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        
        try {
            dynamoDbClient.updateItem(request);
            System.out.println("Product added to Store " + category + " - " + label);
            return findByCategoryAndLabel(category, label);
        } catch (ResourceNotFoundException e) {
            System.err.println("Table does not exist: " + e.getMessage());
            throw e;
        } catch (DynamoDbException e) {
            System.err.println("Error updating products: " + e.getMessage());
            throw e;
        }
    }

    //Update an specific product
    public Store updateOneProduct(String category, String label, String oldProductLabel, String oldProductName, Product newProduct) {
        DynamoDbClient dynamoDbClient = DynamoDBConfig.dynamoDbClient();
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("category", AttributeValue.builder().s(category).build());
        key.put("label", AttributeValue.builder().s(label).build());

        Map<String, AttributeValue> newProductMap = new HashMap<>();
        newProductMap.put("label", AttributeValue.builder().s(newProduct.getLabel()).build());
        newProductMap.put("name", AttributeValue.builder().s(newProduct.getName()).build());
        newProductMap.put("description", AttributeValue.builder().s(newProduct.getDescription()).build());
        newProductMap.put("price", AttributeValue.builder().s(String.valueOf(newProduct.getPrice())).build());

        
        Store store = findByCategoryAndLabel(category, label);
        if (store == null) {
            throw new IllegalArgumentException("Store not found");
        }
        
        int productIndex = -1;
        List<Product> products = store.getProducts() != null ? store.getProducts() : new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getLabel().equals(oldProductLabel) && p.getName().equals(oldProductName)) {
                productIndex = i;
                break;
            }
        }
        
        if (productIndex == -1) {
            throw new IllegalArgumentException("Product not found in store");
        }

        String updateExpression = "SET products[" + productIndex + "] = :newProduct";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newProduct", AttributeValue.builder().m(newProductMap).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("Stores")
                .key(key)
                .updateExpression(updateExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .returnValues("ALL_NEW")
                .build();

        try {
            dynamoDbClient.updateItem(request);
            System.out.println("Product updated atomically in Store " + category + " - " + label);
            return findByCategoryAndLabel(category, label);
        } catch (ResourceNotFoundException e) {
            System.err.println("Table does not exist: " + e.getMessage());
            throw e;
        } catch (DynamoDbException e) {
            System.err.println("Error updating product atomically: " + e.getMessage());
            throw e;
        }
    }

    public List<Store> findAll() {
        return storeTable.scan().items().stream().toList();
    }

    public boolean delete(String category, String label) {
        Store store = findByCategoryAndLabel(category, label);
        if (store.getProducts() != null && !store.getProducts().isEmpty()) {
            for (Product p : store.getProducts()) {
                try {
                    productRepository.delete(p.getLabel(), p.getName());
                } catch (Exception e) {
                    System.err.println("Error deleting product " + p.getLabel() + ": " + e.getMessage());
                }
            }
        }
        storeTable.deleteItem(Key.builder()
            .partitionValue(category)
            .sortValue(label)
            .build());
        return findByCategoryAndLabel(category, label) == null;
    }

    public Store deleteOneProduct(String category, String label, String productLabel, String productName) {
        DynamoDbClient dynamoDbClient = DynamoDBConfig.dynamoDbClient();
        
        Store store = findByCategoryAndLabel(category, label);
        if (store == null) {
            throw new IllegalArgumentException("Store not found");
        }
        
        int productIndex = -1;
        List<Product> products = store.getProducts() != null ? store.getProducts() : new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getLabel().equals(productLabel) && p.getName().equals(productName)) {
                productIndex = i;
                break;
            }
        }
        
        if (productIndex == -1) {
            throw new IllegalArgumentException("Product not found in store");
        }
    
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("category", AttributeValue.builder().s(category).build());
        key.put("label", AttributeValue.builder().s(label).build());
        
        String updateExpression = "REMOVE products[" + productIndex + "]";
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("Stores")
                .key(key)
                .updateExpression(updateExpression)
                .returnValues("ALL_NEW")
                .build();
    
        try {
            dynamoDbClient.updateItem(request);
            System.out.println("Product removed from Store " + category + " - " + label);
            
            try {
                productRepository.delete(productLabel, productName);
            } catch (Exception e) {
                System.err.println("Warning: Could not delete from Products table: " + e.getMessage());
            }
            
            return findByCategoryAndLabel(category, label);
        } catch (ResourceNotFoundException e) {
            System.err.println("Table does not exist: " + e.getMessage());
            throw e;
        } catch (DynamoDbException e) {
            System.err.println("Error removing product from store: " + e.getMessage());
            throw e;
        }
    }
}

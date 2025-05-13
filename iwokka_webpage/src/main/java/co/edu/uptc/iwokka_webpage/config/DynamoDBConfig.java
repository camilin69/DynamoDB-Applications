package co.edu.uptc.iwokka_webpage.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;


@Configuration
public class DynamoDBConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        String endpoint = System.getenv("AWS_DYNAMODB_ENDPOINT");
        System.out.println("Connecting to DynamoDB at: " + endpoint);
        
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(endpoint))
            .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient())
            .build();
    }

    @Bean
    public DynamoDbTable<Store> storeTable(DynamoDbEnhancedClient client) {
        DynamoDbTable<Store> table = client.table("Stores", TableSchema.fromBean(Store.class));
        
        try {
            DescribeTableEnhancedResponse describeTableResponse = table.describeTable();
            System.out.println("Table Stores already exists. It wont be created again.");
        } catch (ResourceNotFoundException e) {
            // Solo crea la tabla si no existe
            table.createTable(b -> b
                .provisionedThroughput(p -> p
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L))
            );
            System.out.println("Table Stores Created.");
        }
        return table;
    }

    @Bean
    public DynamoDbTable<Product> productTable(DynamoDbEnhancedClient client) {
        DynamoDbTable<Product> table = client.table("Products", TableSchema.fromBean(Product.class));

        try {
            DescribeTableEnhancedResponse describeTable = table.describeTable();
            System.out.println("Table Stores already exists. It wont be created again.");
            System.out.println("Describe Table for Products: " + describeTable);


        } catch (ResourceNotFoundException e) {
            table.createTable(b -> b
                .provisionedThroughput(p -> p
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L))
            );
            System.out.println("Table Products Created.");
        }
        return table;
    }
}

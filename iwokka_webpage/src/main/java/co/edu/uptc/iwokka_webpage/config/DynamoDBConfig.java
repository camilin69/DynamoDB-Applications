package co.edu.uptc.iwokka_webpage.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;


@Configuration
public class DynamoDBConfig {

    @Bean
    public static DynamoDbClient dynamoDbClient() {        
        String endpoint = System.getenv("AWS_DYNAMODB_ENDPOINT");
        System.out.println("Connecting to DynamoDB at: " + endpoint);
        
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(endpoint))
            .build();
    }

    @Bean
    public static DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        System.setProperty("spring.devtools.restart.enabled", "false");

        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient())
            .build();
    }

    @Bean
    public DynamoDbTable<Store> storeTable() {
        DynamoDbTable<Store> table = dynamoDbEnhancedClient().table("Stores", TableSchema.fromBean(Store.class));
        
        try {
            DescribeTableEnhancedResponse describeTable = table.describeTable();
            System.out.println("Table Stores already exists. It wont be created again.\nDescribe table for store:" +  describeTable.toString());
        } catch (ResourceNotFoundException e) {
            table.createTable(b -> b
                .provisionedThroughput(p -> p
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L))
                .globalSecondaryIndices(gsi -> gsi
                    .indexName("category-index")
                .provisionedThroughput(p -> p
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L))
                .projection(p -> p.projectionType(ProjectionType.ALL)))
            );
            System.out.println("Table Stores Created.");
        }
        return table;
    }

    @Bean
    public DynamoDbTable<Product> productTable() {
        DynamoDbTable<Product> table = dynamoDbEnhancedClient().table("Products", TableSchema.fromBean(Product.class));

        try {
            DescribeTableEnhancedResponse describeTable = table.describeTable();
            System.out.println("Table Products already exists. It wont be created again.\nDescribe Table for Products: " + describeTable.toString());

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

    @Bean
    public DynamoDbTable<Client> clientTable() {
        DynamoDbTable<Client> table = dynamoDbEnhancedClient().table("Clients", TableSchema.fromBean(Client.class));
        
        try {
            DescribeTableEnhancedResponse describeTable = table.describeTable();
            System.out.println("Table Clients already exists. It wont be created again\nDescribe table for clients: " + describeTable.toString());
            
            // Verificar si el índice existe
            boolean indexExists = describeTable.table().globalSecondaryIndexes().stream()
                .anyMatch(gsi -> gsi.indexName().equals("EmailIndex"));
            System.out.println("IndexExists: " + indexExists);
        } catch (ResourceNotFoundException e) {
            table.createTable(b -> b
                .provisionedThroughput(p -> p
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L))
                .globalSecondaryIndices(gsi -> gsi
                    .indexName("email-index")
                    .provisionedThroughput(p -> p
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L))
                    .projection(p -> p.projectionType(ProjectionType.ALL))));
            System.out.println("Table Clients created.");
        }
        return table;
    }
}

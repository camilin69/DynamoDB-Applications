package co.edu.uptc.iwokka_webpage.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Product {
    private String label;
    private String name;
    private String description;
    private double price;

    public Product() {}

    public String getLabel() {
        return label;
    }

    @DynamoDbPartitionKey
    public void setLabel(String label) {
        this.label = label;
    }
    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbSortKey
    public void setName(String name) {
        this.name = name;
    }
    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @DynamoDbAttribute("price")
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    
}

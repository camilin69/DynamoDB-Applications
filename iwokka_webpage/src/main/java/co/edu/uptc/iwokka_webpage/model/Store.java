package co.edu.uptc.iwokka_webpage.model;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Store {
    private String category;
    private String Label;
    private List<Product> products;

    public Store() {}

    @DynamoDbSecondaryPartitionKey(indexNames = "category-index")
    @DynamoDbPartitionKey
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @DynamoDbSortKey
    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    @DynamoDbAttribute("products")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}

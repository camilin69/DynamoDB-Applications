package co.edu.uptc.iwokka_webpage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductByLabel(String label) {
        return productRepository.findByLabel(label);
    }
}

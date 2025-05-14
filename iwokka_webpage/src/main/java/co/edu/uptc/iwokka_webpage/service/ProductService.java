package co.edu.uptc.iwokka_webpage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product findByLabel(String label) {
        return productRepository.findByLabel(label);
    }

    public List<Product> findAll () {
        return productRepository.findAll();
    }

    public void delete (String label) {
        productRepository.delete(label);
    }
}

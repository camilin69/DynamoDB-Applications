package co.edu.uptc.iwokka_webpage.service;

import java.util.List;
import java.util.Set;

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

    public List<Product> findAllByLabel(String label) {
        return productRepository.findAllByLabel(label);
    }

    public List<Product> findAll () {
        return productRepository.findAll();
    }

    public Set<String> findAllUniqueLabels () {
        return productRepository.findAllUniqueLabels();
    }

    public Product updateProduct(String oldLabel, String oldName, Product newProduct) {
        return productRepository.updateProduct(oldLabel, oldName, newProduct);
    }

    public void delete (String label, String name) {
        productRepository.delete(label, name);
    }
}

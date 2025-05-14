package co.edu.uptc.iwokka_webpage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import co.edu.uptc.iwokka_webpage.repository.ProductRepository;
import co.edu.uptc.iwokka_webpage.repository.StoreRepository;

@Service
public class StoreService {
    @Autowired
    private final StoreRepository storeRepository;
    @Autowired
    private final ProductRepository productRepository;

    public StoreService() {
        this.storeRepository = new StoreRepository();
        this.productRepository = new ProductRepository();
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }

    public Store addProduct(String category, String label, Product product) {
        Store store = storeRepository.findByCategoryAndLabel(category, label);
        productRepository.save(product);
        store.getProducts().add(product);
        return storeRepository.save(store);
    }

    public Store findByCategoryAndLabel(String category, String label) {
        return storeRepository.findByCategoryAndLabel(category, label);
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    
    public void delete (String category, String label) {
        storeRepository.delete(category, label);
    }
}

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

    public StoreService(StoreRepository storeRepository, ProductRepository productRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    public Store addProductToStore(String category, String label, Product product) {
        Store store = storeRepository.findByCategoryAndLabel(category, label);
        productRepository.save(product);
        store.getProducts().add(product);
        return storeRepository.save(store);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
}

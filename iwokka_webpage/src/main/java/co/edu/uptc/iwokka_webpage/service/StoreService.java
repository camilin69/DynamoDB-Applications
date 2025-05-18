package co.edu.uptc.iwokka_webpage.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.iwokka_webpage.model.Client;
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

    public Store updateStore(
        String oldCategory,
        String oldLabel,
        String newCategory,
        String newLabel
    ) {
        return storeRepository.updateStore(oldCategory, oldLabel, newCategory, newLabel);
    }

    public Store updateProducts(String category, String label, Product product) {
        Store store = storeRepository.findByCategoryAndLabel(category, label);
        boolean productExists = false;
        if (store.getProducts() != null) {
            productExists = store.getProducts().stream()
                .anyMatch(p -> p.getLabel().equals(product.getLabel()) && 
                            p.getName().equals(product.getName()));
        }
        
        if (!productExists) {
            Store newStore = storeRepository.updateProducts(category, label, product);
            return newStore;
        } else {
            throw new IllegalArgumentException("Product with label '" + product.getLabel() + 
                                            "' and name '" + product.getName() + 
                                            "' already exists in the store");
        }
    }

    // public void updateClients(String category, String label, Client client) {
    //     Store store = storeRepository.findByCategoryAndLabel(category, label);
        
    //     boolean clientExists = false;
    //     if (store.getClients() != null) {
    //         clientExists = store.getClients().stream()
    //             .anyMatch(c -> c.getId().equals(client.getId())); 
    //     }
        
    //     if (!clientExists) {
    //         storeRepository.updateClients(category, label, client);
    //     } else {
    //         throw new IllegalArgumentException("Client with ID '" + client.getId() + 
    //                                        "' or email '" + client.getEmail() + 
    //                                        "' already exists in the store");
    //     }
    // }

    public Store updateOneProduct (String category, String label, String olProductLabel, String oldProductName, Product newProduct) {
        return storeRepository.updateOneProduct(category, label, olProductLabel, oldProductName, newProduct);
    }

    public Store findByCategoryAndLabel(String category, String label) {
        return storeRepository.findByCategoryAndLabel(category, label);
    }

    public Set<String> findAllUniqueCategories () {
        return storeRepository.findAllUniqueCategories();
    }

    public List<Store> findAllByCategory(String category) {
        return storeRepository.findAllByCategory(category);
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public boolean delete (String category, String label) {
        return storeRepository.delete(category, label);
    }

    public Store deleteOneProduct(String category, String label, String productLabel, String productName) {
        return storeRepository.deleteOneProduct(category, label, productLabel, productName);
    }
}

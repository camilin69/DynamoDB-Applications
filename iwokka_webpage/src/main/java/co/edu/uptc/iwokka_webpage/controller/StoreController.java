package co.edu.uptc.iwokka_webpage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import co.edu.uptc.iwokka_webpage.service.StoreService;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeService.createStore(store);
    }

    @PostMapping("/{category}/{label}/products")
    public Store addProduct(
        @PathVariable String category,
        @PathVariable String label,
        @RequestBody Product product
    ) {
        return storeService.addProductToStore(category, label, product);
    }

    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }
}

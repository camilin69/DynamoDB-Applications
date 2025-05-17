package co.edu.uptc.iwokka_webpage.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.iwokka_webpage.model.Client;
import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.model.Store;
import co.edu.uptc.iwokka_webpage.service.StoreService;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    @Autowired
    private final StoreService storeService;

    public StoreController() {
        this.storeService = new StoreService();
    }

    @PostMapping("/save")
    public Store save(@RequestBody Store store) {
        return storeService.save(store);
    }

    @PutMapping("/updateProducts")
    public Store updateProducts(
        @RequestParam String category,
        @RequestParam String label,
        @RequestBody Product product
    ) {
        return storeService.updateProducts(category, label, product);
    }

    @PutMapping("/updateClients")
    public void updateClients(
        @RequestParam String category,
        @RequestParam String label,
        @RequestBody Client client
    ) {
        storeService.updateClients(category, label, client);
    }

    @GetMapping("/findByCategoryAndLabel")
    public Store findByCategoryAndLabel(@RequestParam String category, @RequestParam String label) {
        return storeService.findByCategoryAndLabel(category, label);
    }

    @GetMapping("/findAllUniqueCategories")
    public Set<String> findAllUniqueCategories() {
        return storeService.findAllUniqueCategories();
    }

    @GetMapping("/findAllByCategory")
    public List<Store> findAllByCategory(@RequestParam String category) {
        return storeService.findAllByCategory(category);
    }

    @GetMapping("/findAll")
    public List<Store> findAll() {
        return storeService.findAll();
    }

    @DeleteMapping("/delete")
    public void delete (@RequestParam String category, @RequestParam String label) {
        storeService.delete(category, label);
    }
}

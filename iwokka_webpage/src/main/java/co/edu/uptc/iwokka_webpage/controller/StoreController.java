package co.edu.uptc.iwokka_webpage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @Autowired
    private final StoreService storeService;

    public StoreController() {
        this.storeService = new StoreService();
    }

    @PostMapping("/save")
    public Store save(@RequestBody Store store) {
        return storeService.save(store);
    }

    @PostMapping("/addProduct")
    public Store addProduct(
        @PathVariable String category,
        @PathVariable String label,
        @RequestBody Product product
    ) {
        return storeService.addProduct(category, label, product);
    }

    @GetMapping("/findByCategoryAndLabel")
    public Store findByCategoryAndLabel(@PathVariable String category, @PathVariable String label) {
        return storeService.findByCategoryAndLabel(category, label);
    }

    @GetMapping("/findAll")
    public List<Store> findAll() {
        return storeService.findAll();
    }

    @DeleteMapping("/delete")
    public void delete (@PathVariable String category, @PathVariable String label) {
        storeService.delete(category, label);
    }
}

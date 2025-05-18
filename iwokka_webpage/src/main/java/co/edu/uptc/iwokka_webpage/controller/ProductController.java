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

import co.edu.uptc.iwokka_webpage.model.Product;
import co.edu.uptc.iwokka_webpage.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductService();
    }

    @PostMapping("/save")
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @GetMapping("/findAllUniqueLabels")
    public Set<String> findAllUniqueLabels () {
        return productService.findAllUniqueLabels();
    }

    @GetMapping("/findAllByLabel")
    public List<Product> findByLabel(@RequestParam String label) {
        return productService.findAllByLabel(label);
    }

    @GetMapping("/findAll") 
    public List<Product> findAll () {
        return productService.findAll();
    }

    @PutMapping("/updateProduct")
    public Product updateProduct (@RequestParam String oldLabel, @RequestParam String oldName, @RequestBody Product newProduct) {
        return productService.updateProduct(oldLabel, oldName, newProduct);
    }

    @DeleteMapping("/delete")
    public void delete (@RequestParam String label, @RequestParam String name) {
        productService.delete(label, name);
    }
}
